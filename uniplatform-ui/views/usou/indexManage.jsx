import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Text,Pagination,context,Select,Option,Textarea,
    Modal,ModalHeader,ModalBody,ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,Checkbox } from 'epm-ui';
import { SingleBanner,util_bytesToSize,Footer,COMM_HeadBanner,UsouMenu,NavigationBar } from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'Usou',
    css: [
        'css/indices.min.css',
        'css/singleBanner.min.css',
        'css/leftnav.min.css'
    ],
    js: [
        'js/ace/ace.min.js',
        'js/ace/mode-json.min.js'
    ]
};

class indexManage extends Component {
    constructor(props){
        super(props);
        this.state = {
            index: 1,
            size: 10,
            total:0,
            totalData:[],
            indexStats:[],
            state:[],
            indiceList:[],
            newVisible:false,
            visible: false,
            openVisible: false,
            deleteVisible: false,
            cleanVisible: false,
            refreshVisible: false,
            updateVisible: false,
            data: null,
            indicesName:[],
            shards:0,
            replicas:0
        }
        this.handleSelect = this.handleSelect.bind( this );
        this.indicesDetail = this.indicesDetail.bind(this);
        this.exchangePage =  this.exchangePage.bind(this);
        this.beforeSubmit = this.beforeSubmit.bind(this);
        this.afterSubmit = this.afterSubmit.bind( this );
        this.afterUpdateSubmit = this.afterUpdateSubmit.bind( this );
        this.beforeSubmitSet = this.beforeSubmitSet.bind(this);
        this.beforeSubmitMap = this.beforeSubmitMap.bind(this);
        this.handleClick=this.handleClick.bind(this);
        this.formGetterInput=this.formGetterInput.bind(this);
    }
    componentDidMount() {
        this.fetchUsouIndexList();
    }

    //获取索引列表
    fetchUsouIndexList() {
        let { index,size } = this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_overview', {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let {indexStats, state} = data;
                this.setState({indexStats, state});
                let totalData = this.getCountIndice();
                this.setState( { totalData } );
                this.exchangePage(index,size);
                this.getIndiceName();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //索引列表分页
    exchangePage(index,size){
        let indiceList = [];
        let totalData = this.state.totalData;
        let total = totalData.length;
        this.setState({index,size,total});
        for(let i = (index-1) * size; i < index * size; i++){
            if(totalData[i]){
                indiceList.push(totalData[i])
            }
        }
        this.setState({indiceList});
    }

    getCountIndice(){
        let {indexStats,state} = this.state;
        if(indexStats.hasOwnProperty("indices")){
            let indices = Object.keys(state.metadata.indices).map(function (indicesName){
                let a = indexStats.indices[indicesName];
                let aa =state.routing_table.indices[indicesName];
                let byte = 0 + 'KB', doc = 0;
                let shard = 0, copie = 0;
                if(a){
                    doc = indexStats.indices[indicesName].primaries.docs.count;
                    byte = util_bytesToSize(indexStats.indices[indicesName].primaries.store.size_in_bytes);
                }
                if(aa){
                    shard = Object.keys(state.routing_table.indices[indicesName].shards).length;
                    copie = state.routing_table.indices[indicesName].shards[0].length-1;
                }

                let left_indices = {
                    indicesName :  indicesName,
                    docs : doc,
                    bytes: byte,
                    indicesState : state.metadata.indices[indicesName].state,
                    shards : shard,
                    copies : copie
                }
                return left_indices;
            });
            return indices;
        }else {
            popup( <Snackbar message="抱歉，暂时没有数据" /> );
        }

    }

    //添加索引
    showNewModal(){
        this.setState( { newVisible: true } );
    }
    closeNewModal() {
        this.resetAdd();
        this.setState( { newVisible: false } );
    }
    indicesValue(indiceName) {
        this.setState({indiceName});
    }
    getIndiceName(){
        let indicesName = this.getIndice();
        this.setState({indicesName});
    }
    getIndice(){
        let {indexStats,state} = this.state;
        let indices = Object.keys(state.metadata.indices).map(function (indicesName){
            let left_indices = {
                value :  indicesName,
                text : indicesName
            }
            return left_indices;
        });
        return indices;
    }
    handleValue(name) {
        fetch( Uniplatform.context.url +  '/usou/cluster/'+this.props.page.id+'/'+ name, {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let  settings  = [];
                settings.push(data);
                this.setState( { settings : settings } );
                this.getSetting();
                this.getUpdate();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    getSetting() {
        let setText = {};
        let settings = this.state.settings;
        Object.keys(settings).map(function(indicesName){
            Object.keys(settings[indicesName]).map(function(index){
                let t = {
                    settings : settings[indicesName][index].settings,
                    mappings : settings[indicesName][index].mappings
                }
                for(let key in t){
                    setText[key] = t[key];
                }
            });
        });
        let res = this.check(setText);
        this.setState({res});
    }
    beforeSubmit(data){
        let shards = data.number_of_shards;
        let replicas = data.number_of_replicas;
        if(/^\d*$/g.test(shards) || shards=='' || shards.length == 0 ){
            if(/^\d*$/g.test(replicas) || replicas=='' || replicas.length == 0){
                if(data.body == ''){
                    let bodys = '{"settings": {"index": {"number_of_shards": "'+shards+'", "number_of_replicas": "'+replicas+'"}}}';
                    let body = "body";
                    data[body] = bodys;
                    return data;
                }
                return data;
            }else {
                popup( <Snackbar message="副本数必须输入数字！" /> );
                return false;
            }
        }else{
            popup( <Snackbar message="主分片数必须输入数字！" /> );
            return false;
        }
    }
    afterSubmit(data){
        this.setState( { newVisible: false } );
        if(data.status == '200'){
            popup(<Snackbar message="添加成功" />);
            this.fetchUsouIndexList();
            this.resetAdd();
        }else{
            popup(<Snackbar message={data.message} />);
        }
    }
    //格式化json数据
    check(text_value){
        text_value = JSON.stringify(text_value);
        let res="";
        for(let i=0,j=0,k=0,ii,ele;i<text_value.length;i++){//k:缩进，j:""个数
            ele=text_value.charAt(i);
            if(j%2==0&&ele=="}"){
                k--;
                for(ii=0;ii<k;ii++){
                    ele="    "+ele;
                }
                ele="\n"+ele;
            }else if(j%2==0&&ele=="{"){
                ele+="\n";
                k++;
                for(ii=0;ii<k;ii++)
                    ele+="    ";
            }else if(j%2==0&&ele==","){
                ele+="\n";
                for(ii=0;ii<k;ii++)
                    ele+="    ";
            }else if(ele=="\""){
                j++;
            }
            res+=ele;
        }
        return res;
    }

    //关闭索引
    closeIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/_close', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageClose ='';
                if ( data.status == 200){
                    messageClose = '操作成功！';
                } else {
                    messageClose = '操作失败';
                }
                let { message } = data;
                this.setState( { mClose:message,messageClose:messageClose } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //打开索引
    openIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/_open', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageOpen ='';
                if ( data.status == 200){
                    messageOpen = '操作成功！';
                } else {
                    messageOpen = '操作失败';
                }
                let { message } = data;
                this.setState( { mOpen:message,messageOpen:messageOpen } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //删除索引
    deleteIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/delete', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageDelete ='';
                if ( data.status == 200){
                    messageDelete = '操作成功！';
                } else {
                    messageDelete = '操作失败';
                }
                let { message } = data;
                this.setState( { mDelete:message,messageDelete:messageDelete } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //清理缓存
    cleanIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/_clearCache', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageClean ='';
                if ( data.status == 200){
                    messageClean = '操作成功！';
                } else {
                    messageClean = '操作失败';
                }
                let { message } = data;
                this.setState( { mClean:message,messageClean:messageClean } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //刷新索引
    refreshIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/_refresh', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageRe ='';
                if ( data.status == 200){
                    messageRe = '操作成功！';
                } else {
                    messageRe = '操作失败';
                }
                let { message } = data;
                this.setState( { mRefresh:message,messageRe:messageRe } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    showCloseModal(name){
        let selected = this.state.data;
        if(selected == '' || selected.length == 0){
            popup( <Snackbar message="您未选择任何索引！" /> );
        }else {
            this.closeIndices(name);
            this.setState( { visible: true } );
        }
    }
    handleClose() {
        this.fetchUsouIndexList();
        this.setState( { visible: false } );
    }
    showOpenModal(name){
        let selected = this.state.data;
        if(selected == '' || selected.length == 0){
            popup( <Snackbar message="您未选择任何索引！" /> );
        }else {
            this.openIndices(name);
            this.setState( { openVisible: true } );
        }
    }
    handleOpen_Close() {
        this.fetchUsouIndexList();
        this.setState( { openVisible: false } );
    }
    showDeleteModal(name){
        let selected = this.state.data;
        if(selected == '' || selected.length == 0){
            popup( <Snackbar message="您未选择任何索引！" /> );
        }else {
            const confirmShow = confirm("确定要删除索引?");
            if(!confirmShow){
                return false;
            }else {
                this.deleteIndices(name);
                this.setState( { deleteVisible: true } );
            }
        }
    }
    handleDeleteClose() {
        this.fetchUsouIndexList();
        this.setState( { deleteVisible: false } );
    }
    showCleanModal(name){
        let selected = this.state.data;
        if(selected == '' || selected.length == 0){
            popup( <Snackbar message="您未选择任何索引！" /> );
        }else {
            this.cleanIndices(name);
            this.setState({ cleanVisible: true });
        }
    }
    handleCleanClose(){
        this.fetchUsouIndexList();
        this.setState({ cleanVisible: false });
    }
    showRefreshModal(name) {
        let selected = this.state.data;
        if(selected == '' || selected.length == 0){
            popup( <Snackbar message="您未选择任何索引！" /> );
        }else {
            this.refreshIndices(name);
            this.setState({refreshVisible: true});
        }
    }
    handleRefreshClose(){
        this.fetchUsouIndexList();
        this.setState({ refreshVisible: false });
    }

    handleSelect( data ) {
        this.setState( { data: data.map( ( key ) => key.indicesName ).join( ',' ) } );
    }

    //索引详情页跳转
    indicesDetail(name){
        let {indiceList} = this.state;
        let indiceState;
        for(let i = 0; i<indiceList.length; i++){
            if(indiceList[i].indicesName == name){
                indiceState = indiceList[i].indicesState;
            }
        }
        if(indiceState == 'open'){
            window.open('/usou/indicesDetail?id='+this.props.page.id+'&name='+ this.props.page.name+'&indicesName='+name);
        }else {
            popup( <Snackbar message="该索引当前为关闭状态，请开启后再查看详情！" /> );
        }
    }

    //索引编辑
    showUpdateModal(name){
        let selectName = name;
        this.handleValue(name);
        this.setState( { updateVisible: true, selectName:selectName } );
    }
    closeUpdateModal() {
        this.resetEdit();
        this.setState( { updateVisible: false } );
    }




    //编辑索引——————————————————————————————分割线
    getUpdate() {
        let setText = {};
        let mapText = {};
        let shards = 0, replicas = 0;
        let settings = this.state.settings;
        Object.keys(settings).map(function(indicesName){
            Object.keys(settings[indicesName]).map(function(index){
                let set = {
                    settings : settings[indicesName][index].settings,
                }
                let map = {
                    mappings : settings[indicesName][index].mappings
                }
                let n ={
                    number_of_shards: settings[indicesName][index].settings.index.number_of_shards,
                    number_of_replicas: settings[indicesName][index].settings.index.number_of_replicas,
                }
                shards = n.number_of_shards;
                replicas = n.number_of_replicas;
                for(var key in set){
                    setText[key] = set[key];
                }
                for(var key in map){
                    mapText[key] = map[key];
                }

            });
        });
        let settingText = this.check(setText);
        let mappingText = this.check(mapText);
        this.setState({settingText,mappingText,mapText,shards,replicas});
        this.edit();
    }

    beforeSubmitSet(data){
        let shards = data.number_of_shards;
        let replicas = data.number_of_replicas;
        let editor = ace.edit("code");
        let settingValue = editor.getValue();
        if(/^\d*$/g.test(replicas) || replicas=='' || replicas.length == 0){
            if(settingValue == ''){
                let bodys = '{"index": {"number_of_shards": "'+shards+'", "number_of_replicas": "'+replicas+'"}}';
                let body = "body";
                data[body] = bodys;
                return data;
            }else {
                let setValueJson = eval('(' + settingValue + ')');
                let set_value = setValueJson.settings;
                let bodys = JSON.stringify(set_value);
                let body = "body";
                data[body] = bodys;
                return data;
            }
            return data;
        }else {
            popup( <Snackbar message="副本数必须输入数字！" /> );
            return false;
        }
    }

    beforeSubmitMap(data){
        let editormap = ace.edit("codemap");
        let mapValue = editormap.getValue();
        let bodys = mapValue;
        let body = "body";
        data[body] = bodys;
        return data;
    }

    afterUpdateSubmit(data){
        if(data.status == '200'){
            popup(<Snackbar message="修改成功" />);
        }else{
            popup(<Snackbar message={data.message} />);
        }
        this.handleValue(this.state.selectName);
    }

    //ace编辑器
    edit(){
        //初始化对象
        let settingText = this.state.settingText;
        let mappingText = this.state.mappingText;
        let editor = ace.edit("code");
        let editormap = ace.edit("codemap");
        //设置语言
        let language = "json";
        editor.session.setMode("ace/mode/" + language);
        editormap.session.setMode("ace/mode/" + language);
        //设置编辑器中的值
        editor.setValue(settingText);
        editormap.setValue(mappingText);
        //字体大小
        editor.setFontSize(18);
        editormap.setFontSize(18);
        //设置只读（true时只读，用于展示代码）
        editor.setReadOnly(false);
        editormap.setReadOnly(false);
        //自动换行,设置为off关闭
        editor.setOption("wrap", "free");
        editormap.setOption("wrap", "free");
    }

    //根据索引名称查询索引
    handleKeyPress( event ) {
        if (event.which==13){
            let input = this.getValueInput();
            this.setState({ keywords : input }, () => this.handleClick());
        }
    }
    search(){
        let input = this.getValueInput();
        this.setState({ keywords : input }, () => this.handleClick());
    }
    handleClick() {
        let {keywords} = this.state;
        let { index,size } = this.state;
        index=1;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_overview/'+ keywords, {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let {indexStats, state} = data;
                this.setState({indexStats, state});
                let totalData = this.getCountIndice();
                this.setState( { totalData } );
                this.exchangePage(index,size);
                this.getIndiceName();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    formGetterInput(getter){
        this.getValueInput=getter.value;
    }
    wordSearchAuto(data){
        if(data == ''){
            this.setState({ keywords : '' }, () => this.handleClick());
        }
    }
    searchAuto(){
        this.setState({ keywords : '' }, () => this.handleClick());
    }
    //清空回显
    formTirggerAdd( trigger ) {
        this.resetAdd = trigger.reset;
    }
    formTirggerEdit( trigger ) {
        this.resetEdit = trigger.reset;
    }
    render() {
        let {indiceList,index,size,total,newVisible,visible,openVisible,deleteVisible,cleanVisible, refreshVisible,data,
            mClose,messageClose,mOpen,messageOpen,mDelete,messageDelete,mClean,messageClean,mRefresh,messageRe,
            indexStats, state,indicesName,settings,indiceName,res,updateVisible,
            setting_echo,settingText,mappingText,shards,replicas,typeName,selectName} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Row style={{minHeight:'700px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <Divider/>
                        <UsouMenu url={'/usou/indexManage'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                       <Divider/>
                        <NavigationBar code={'indexManage'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        <Row>
                            <Button type="default" size="tiny" onClick={this.showNewModal.bind(this) } style={{float:'left',margin:'10px auto',position:'relative',zIndex:1}} >
                                <Icon icon="plus"/> 新建索引
                            </Button>
                            <Button type="default" size="tiny" onClick={this.showOpenModal.bind(this,this.state.data) } style={{margin:'10px',position:'relative',zIndex:1}} >
                                <Icon icon="folder-open" />打开索引
                            </Button>
                            <Button type="default" size="tiny" onClick={this.showCloseModal.bind(this,this.state.data) } style={{margin:'10px',position:'relative',zIndex:1}} >
                                <Icon icon="close" />关闭索引
                            </Button>
                             <Button type="default" size="tiny" onClick={this.showDeleteModal.bind(this,this.state.data) } style={{margin:'10px',position:'relative',zIndex:1}} >
                                <Icon icon="trash-o" /> 删除索引
                            </Button>
                             <Button type="default" size="tiny" onClick={this.showCleanModal.bind(this,this.state.data) } style={{margin:'10px',position:'relative',zIndex:1}} >
                                <Icon icon="eraser" />清理缓存
                            </Button>
                             <Button type="default" size="tiny" onClick={this.showRefreshModal.bind(this,this.state.data) } style={{margin:'10px',position:'relative',zIndex:1}} >
                                <Icon icon="refresh" />刷新索引
                            </Button>
                            <Button type="primary" style={{float:'right'}} onClick={this.search.bind(this)}>搜索</Button>
                             <Col size={6} style={{position: 'relative',float:'right',zIndex: '1'}}>
                                <Input placeholder="请输入要查询的索引名称"
                                       onKeyPress={ this.handleKeyPress.bind(this)} type={'search'}
                                       getter={ this.formGetterInput }
                                       onChange={this.wordSearchAuto.bind(this)}
                                       onClear={this.searchAuto.bind(this)}/>
                            </Col>
                        </Row>
                       
                        <Table dataSource={ indiceList } checkable onCheck={ this.handleSelect } striped={ true } multiLine={ true } >
                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                { ( value, index ) => { return Number(index) + 1; } }
                            </Column>
                            <Column title="索引名" scaleWidth = '20%'textAlign="center">
                                { ( value ,indicesName) => <a onClick={this.indicesDetail.bind(this,value.indicesName,this.props.page.name)}>{ value.indicesName }</a> }
                            </Column>
                            <Column title="文档数量" dataIndex="docs"  scaleWidth = '15%'textAlign="center"/>
                            <Column title="占用空间" dataIndex="bytes" scaleWidth = '10%'textAlign="center"/>
                            <Column title="主分片数" dataIndex="shards" scaleWidth = '10%'textAlign="center"/>
                            <Column title="副本数" dataIndex="copies" scaleWidth = '10%'textAlign="center"/>
                            <Column title="索引状态" dataIndex="indicesState" scaleWidth = '15%'textAlign="center"/>
                            <Column title="索引配置" scaleWidth = '10%' textAlign="center">
                                { ( value ) => {
                                    return (
                                        <div>
                                            <Button type="default" size="tiny" onClick={this.showUpdateModal.bind(this,value.indicesName)}>
                                                <Icon icon="edit" /> 编辑
                                            </Button>
                                        </div>
                                    )
                                } }
                            </Column>
                        </Table>
                        <Divider />
                        <Pagination index={ index }  total={ total>1000?1000:total } size={ size } align='right' onChange={this.exchangePage}/>
                        <Divider />
                    </Col>
                </Row>

                <Form method="post"
                      action={ `${contextUrl}/usou/cluster/${this.props.page.id}/${this.state.indiceName}/create`}
                      async={true}
                      onSubmit={ this.beforeSubmit }
                      onAfterSubmit={this.afterSubmit}
                      trigger={ this.formTirggerAdd.bind(this) }
                >
                {
                        newVisible?
                        <Modal size="large" onClose={ this.closeNewModal.bind( this ) }>
                            <ModalHeader>
                                新建索引
                            </ModalHeader>
                            <ModalBody>
                                <Row style={{margin:'2.5%'}}>
                                    <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }}>
                                        <FormItem required={true}>
                                            <Label style={{width:'initial'}}>索引名称</Label>
                                            <Input style={{ display:'block' }} name="indicesName" placeholder="请输入索引名称" onChange={this.indicesValue.bind(this)}/>
                                        </FormItem>
                                        <FormItem>
                                            <Label style={{width:'initial'}}>主分片数</Label>
                                            <Input style={{ display:'block' }} name="number_of_shards" placeholder="请输入分片数"/>
                                        </FormItem>
                                        <FormItem>
                                            <Label style={{width:'initial'}}>副本数</Label>
                                            <Input style={{ display:'block' }} name="number_of_replicas" placeholder="请输入副本数"/>
                                        </FormItem>
                                        <FormItem>
                                            <Label style={{width:'initial'}}>索引列表</Label>
                                            <Select style={{ display:'block', textAlign:'left' }} name="indicesList" dataSource={ indicesName } placeholder="请选择索引" onChange={this.handleValue.bind(this)}/>
                                        </FormItem>
                                    </Col>
                                    <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }}>
                                        <FormItem name="textarea">
                                            <Label style={{width:'initial'}}>索引定义</Label>
                                            <Textarea id="content_value" name="body" rows={16} style={{display:'block',resize:'none',width:'500px'}} placeholder="请输入设置" value={res}/>
                                        </FormItem>
                                    </Col>
                                </Row>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={ this.closeNewModal.bind( this ) }>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                        :
                        null
                }
                </Form>
                <Modal visible={ visible } onClose={ this.handleClose.bind( this ) } >
                    <ModalHeader>
                        关闭索引
                    </ModalHeader>
                    <ModalBody height={ 200 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.data }</span>
                        <p>{messageClose}</p>
                        <p>{ mClose }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>
                <Modal visible={ openVisible } onClose={ this.handleOpen_Close.bind( this ) } >
                    <ModalHeader>
                        打开索引
                    </ModalHeader>
                    <ModalBody height={ 200 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.data }</span>
                        <p>{messageOpen}</p>
                        <p>{ mOpen }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleOpen_Close.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>

                <Modal visible={ deleteVisible } onClose={ this.handleDeleteClose.bind( this ) } >
                    <ModalHeader>
                        删除
                    </ModalHeader>
                    <ModalBody height={ 300 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.data }</span>
                        <p>{messageDelete}</p>
                        <p>{ mDelete }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleDeleteClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>
                <Modal visible={ cleanVisible } onClose={ this.handleCleanClose.bind( this ) } >
                    <ModalHeader>
                        清理缓存
                    </ModalHeader>
                    <ModalBody height={ 300 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.data }</span>
                        <p>{messageClean}</p>
                        <p>{ mClean }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleCleanClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>
                <Modal visible={ refreshVisible } onClose={ this.handleRefreshClose.bind( this ) } >
                    <ModalHeader>
                        刷新
                    </ModalHeader>
                    <ModalBody height={ 300 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.data }</span>
                        <p>{messageRe}</p>
                        <p>{ mRefresh }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleRefreshClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>

                <Modal visible={ updateVisible } className="maxHeight" size="large" onClose={ this.closeUpdateModal.bind( this ) } >
                    <ModalHeader>
                        编辑索引
                    </ModalHeader>
                    <ModalBody>
                        <Row className="indices_detail_row">
                            <div style={{margin:'0 auto'}}>
                                <Form type="horizontal"
                                      method="post"
                                      action={ contextUrl + '/usou/cluster/'+this.props.page.id+'/'+ selectName +'/_setting/update'}
                                      async={true}
                                      onSubmit={ this.beforeSubmitSet }
                                      onAfterSubmit={this.afterUpdateSubmit}
                                >
                                    <FormItem style={{display:'inline-block',width:'28%',margin:'2.5%'}}>
                                        <Label style={{width:'initial'}}>索引名称</Label>
                                        <Input style={{ display:'block' }} name="indicesName" value={ selectName } disabled/><br/>
                                    </FormItem>
                                    <FormItem style={{display:'inline-block',width:'28%',margin:'2.5%'}}>
                                        <Label style={{width:'initial'}}>主分片数</Label>
                                        <Input style={{ display:'block' }} name="number_of_shards" value={shards} disabled/><br/>
                                    </FormItem>
                                    <FormItem style={{display:'inline-block',width:'28%',margin:'2.5%'}}>
                                        <Label style={{width:'initial'}}>副本数</Label>
                                        <Input style={{ display:'block' }} name="number_of_replicas" value={replicas}  trigger={ this.formTirggerEdit.bind(this) }/><br/>
                                    </FormItem>
                                    <FormItem style={{textAlign:'left',display:'inline-block',width:'95%',margin:'2.5%'}}>
                                        <div style={{ border:'1px solid #c9c9c9',borderTopColor:'white'}}>
                                            <div className="cluster_detail_col">
                                                <div className = "cluster_detail_title" >
                                                    <span className = "cluster_detail_span"> <Icon icon="bars" /> 索引设置</span>
                                                </div>
                                            </div>
                                            <pre id="code" className="ace_editor" style={{minHeight:'400px'}}>
                                                <Textarea className="ace_text-input"></Textarea>
                                            </pre>
                                        </div>
                                    </FormItem>
                                    <div>
                                        <Button type="primary" htmlType="submit" style={{margin:'2.5%'}}>索引配置</Button>
                                    </div>
                                </Form>

                                <Form type="horizontal"
                                      method="post"
                                      action={ contextUrl + '/usou/cluster/'+this.props.page.id+'/'+ selectName +'/_mapping/put'}
                                      async={true}
                                      onSubmit={ this.beforeSubmitMap }
                                      onAfterSubmit={this.afterUpdateSubmit}
                                >
                                    <Row>
                                        <FormItem style={{textAlign:'left',display:'inline-block',width:'95%',margin:'2.5%'}}>
                                            <div style={{ border:'1px solid #c9c9c9',borderTopColor:'white'}}>
                                                <div className="cluster_detail_col">
                                                    <div className = "cluster_detail_title" >
                                                        <span className = "cluster_detail_span"> <Icon icon="map" /> 添加类型</span>
                                                    </div>
                                                </div>
                                                <pre id="codemap" className="ace_editor" style={{minHeight:'400px'}}>
                                                    <Textarea className="ace_text-input"></Textarea>
                                                </pre>
                                            </div>
                                        </FormItem>
                                    </Row>
                                    <div>
                                        <Button type="primary" style={{margin:'2.5%'}} onClick={this.closeUpdateModal.bind(this)}>关闭</Button>
                                        <Button type="primary" htmlType="submit" style={{margin:'2.5%'}} onClick={this.handleValue.bind(this)}>添加类型</Button>
                                    </div>
                                </Form>
                            </div>
                        </Row>
                    </ModalBody>
                </Modal>
                <Footer/>
            </Page>
        );
    }
}
indexManage.UIPage = page;
export default indexManage;
