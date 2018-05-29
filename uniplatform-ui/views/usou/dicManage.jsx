import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Text,Pagination,context,Select,Option,Textarea,
    Modal,ModalHeader,ModalBody,ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,Checkbox,Dialog } from 'epm-ui';
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
            replicas:0,
            url:null,
            message:null,
            status:null,
            resdata:[],
            selectdic:null,
            selecttype:null,
            selectstatus:null

        }
        this.handleSelect = this.handleSelect.bind( this );
        this.dicDetail = this.dicDetail.bind(this);
        this.exchangePage =  this.exchangePage.bind(this);
        this.beforeSubmit = this.beforeSubmit.bind(this);
        this.beforeUpdateSubmit = this.beforeUpdateSubmit.bind(this);
        this.afterSubmit = this.afterSubmit.bind( this );
        this.afterUpdateSubmit = this.afterUpdateSubmit.bind( this );
        this.beforeSubmitSet = this.beforeSubmitSet.bind(this);
        this.beforeSubmitMap = this.beforeSubmitMap.bind(this);
        this.handleClick=this.handleClick.bind(this);
        this.formGetterInput=this.formGetterInput.bind(this);

        this.formTirggerWord=this.formTirggerWord.bind(this);
        this.formTirggerWordEdit = this.formTirggerWordEdit.bind(this);
        this.handleDivision = this.handleDivision.bind( this );
    }
    componentDidMount() {
        this.fetchUsouIndexList();
    }

    //获取索引列表
    fetchUsouIndexList() {
         let { index,size } = this.state;
         let datarequest = new FormData();
         // datarequest.append("body", '{type:"dic"}');
         datarequest.append("body", '{type:""}');
        fetch( contextUrl + '/usou/cluster/'+this.props.page.id+'/_analyze/dic/list', {
            credentials: 'same-origin',
            method: 'POST',body:datarequest
        }).then( ( res ) => res.json())
          .then( ( data ) => {
                let resdata = data.data;
                this.setState({totalData:resdata});
                this.exchangePage(index,size);
                this.getIndiceName();
            })
        .catch( ( err ) => console.log( err.toString() ) );
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
        let {status,message,data,url} = this.state;
        if(status=="200"){
        let indices = data;
        let datalist=[];
        for(var a=0;a<indices.length;a++){

                let dicType = indices[a].type;
                let dicName = indices[a].defineDic;
                let status = indices[a].status;
                if(status==1){
                    status="开启";
                }else{
                    status="关闭";
                }
                let left_indices = {
                    dicName :  dicName,
                    dicType : dictype,
                    status  : status
                }
                datalist.push(left_indices);

         }    
                return datalist;
        }else {
            popup( <Snackbar message="抱歉，暂时没有数据" /> );
        }

    }

    //添加索引
    showNewModal(){
        this.setState( { newVisible: true } );
    }
    closeNewModal() {
        this.resetWord();
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
        let data = this.state.totalData;
        if(data.length>0){
        let indices = data;
        let datalist=[];
        for(var a=0;a<indices.length;a++){
                let defineDic = indices[a].defineDic;       
                let left_indices = {
                    value :  defineDic,
                    text : defineDic
                }
                datalist.push(left_indices);
         }    
                return datalist;
        }else {
            popup( <Snackbar message="抱歉，暂时没有数据!" /> );
        }

    }
    handleValue(name) {
        fetch( contextUrl +  '/usou/cluster/'+this.props.page.id+'/'+ name, {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let  settings  = [];
                settings.push(data);
                this.setState( { settings : settings } );
                
                this.getUpdate();
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    beforeSubmit(data){
         if (data.dic==null||data.dic==""){
              popup(<Dialog  message="请输入词库名称"/>);
        }else{
            let str = JSON.stringify(data);
            data['body'] = str;
            this.setState( { newVisible: false } );
            this.resetWord();
            return data;
        }
    }
    beforeUpdateSubmit(data){
        if(data.type=='分词词库'){
            data.type='dic';
        }else if(data.type=='同义词词库'){
            data.type='synonyms';
        }else{
            data.type='stop';
        }
         data.dic=this.state.selectdic;
         if (data.update==null||data.update==""){
              popup(<Dialog  message="请输入词库名称"/>);
        }else{
            let str = JSON.stringify(data);
            let data1={};
            data1['body'] = str;
            this.setState( { newVisible: false } );
            return data1;
        }
    }

     formTirggerWord(trigger){
        this.resetWord=trigger.reset;
    }

    formTirggerWordEdit(trigger){
        this.resetWordEdit=trigger.reset;
    }

    afterSubmit(data){
        this.setState( { newVisible: false } );
        if(data.status == 200){
            popup(<Snackbar message="添加成功" />);
            this.fetchUsouIndexList();
        }else{
            popup(<Snackbar message={data.message} />);
        }
    }


    //关闭词库
    closeIndices(){
        let data = new FormData();
        let dic = this.state.selectdic;
        let type = this.state.selecttype;
        data.append("body",'{type:"'+ type +'",dic:"'+ dic +'"}');


 fetch( contextUrl+ '/usou/cluster/' + this.props.page.id + '/_analyze/dic/disable', 
 {    credentials: 'same-origin',  method: 'POST' ,  body:data    } )
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
    //打开词库
    openIndices(){
        let data = new FormData();
        let dic = this.state.selectdic;
        let type = this.state.selecttype;
        data.append("body",'{type:"'+ type +'",dic:"'+ dic +'"}');

fetch( contextUrl+ '/usou/cluster/' + this.props.page.id + '/_analyze/dic/enable', 
 {    credentials: 'same-origin',  method: 'POST' , body:data   } )
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
    //删除词库
    deleteIndices(){
    let data = new FormData();
        let dic = this.state.selectdic;
        let type = this.state.selecttype;
        data.append("body",'{type:"'+ type +'",dic:"'+ dic +'"}');

fetch( contextUrl+ '/usou/cluster/' + this.props.page.id + '/_analyze/dic/delete', 
 {    credentials: 'same-origin',  method: 'POST' , body:data   } )

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
 


    showCloseModal(rowvalue){
        let selectdic = rowvalue[0];
        let selecttype = rowvalue[1];
        this.setState( { selectdic:selectdic,selecttype:selecttype},()=>{this.closeIndices();this.setState( { visible: true } );} );

    }

    handleClose() {
        this.fetchUsouIndexList();
        this.setState( { visible: false } );
    }

    showOpenModal(rowvalue){
        let selectdic = rowvalue[0];
        let selecttype = rowvalue[1];
        this.setState( { selectdic:selectdic,selecttype:selecttype},()=>{this.openIndices();this.setState( { openVisible: true } );} );
    }
    handleOpen_Close() {
        this.fetchUsouIndexList();
        this.setState( { openVisible: false } );
    }
    showDeleteModal(rowvalue){
        let selectdic = rowvalue[0];
        let selecttype = rowvalue[1];
        this.setState( { selectdic:selectdic,selecttype:selecttype} );
        const confirmShow = confirm("确定要删除该词库?");
        if(!confirmShow){
                return false;
        }else{
            this.setState( { selectdic:selectdic,selecttype:selecttype},()=>{this.deleteIndices(); this.setState( { deleteVisible: true } );} );
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
        this.setState( { 
            data: data.map( ( key ) => key.type+","+key.defineDic ).join( ',' ) 
        } );
    }

    //索引详情页跳转
    dicDetail(rowvalue){
        let selectdic = rowvalue[0];
        let selecttype = rowvalue[1];
        let selectstatus = rowvalue[2];
        this.setState( { selectdic:selectdic,selecttype:selecttype} );

        if(selectstatus == '1'){
           this.setState({ selectdic:selectdic,selecttype:selecttype},
                        ()=>{
                                window.open('/usou/participleManage_Dictionary?id='+this.props.page.id+'&name='
                                + this.props.page.name+'&type='+selecttype+'&dic='+selectdic,'_self');
            });
            
        }else {
            popup( <Snackbar message="该词库当前为关闭状态，请开启后再查看详情！" /> );
        }
    }

    //索引编辑
    showUpdateModal(rowvalue){
        let selectdic = rowvalue[0];
        let selecttype = rowvalue[1];
        this.setState( { selectdic:selectdic,selecttype:selecttype} );
        this.setState( { updateVisible: true } );
    }
    closeUpdateModal() {
        this.resetWordEdit();
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
            this.fetchUsouIndexList();
            popup(<Snackbar message="修改成功" />);
            this.setState( { updateVisible: false },()=>this.resetWordEdit());
        }else{
            popup(<Snackbar message={data.message} />);
        }
        //this.handleValue(this.state.selectdic);
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
            this.handleClick();
        }
    }

    handleClick() {
        let input = this.getValueInput();
        let { index,size } = this.state;
        index=1;
        fetch( contextUrl + '/usou/cluster/'+this.props.page.id+'/_overview/'+ input, {
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

    handleDivision( rowData ){
        let quotient =  rowData.defineDic+","+rowData.type+","+rowData.status ;
        return (<span style={ { color: "#c33" } } >{ quotient }</span>);
    }

    render() {
        let {indiceList,index,size,total,newVisible,visible,openVisible,deleteVisible,cleanVisible, refreshVisible,data,
            mClose,messageClose,mOpen,messageOpen,mDelete,messageDelete,mClean,messageClean,mRefresh,messageRe,
            indexStats, state,indicesName,settings,indiceName,res,updateVisible,
            setting_echo,settingText,mappingText,shards,replicas,typeName,selectdic,selecttype,selectstatus} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Row style={{minHeight:'700px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <Divider/>
                        <UsouMenu url={'/usou/dicManage'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                       <Divider/>
                        <NavigationBar code={'dicManage'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        <Row>
                            <Button type="default" size="tiny" onClick={this.showNewModal.bind(this) } style={{float:'left',margin:'10px auto',position:'relative',zIndex:1}} >
                                <Icon icon="plus"/> 新建词库
                            </Button>                       
                        </Row>

                        <Table dataSource={ indiceList } checkDisabled  onCheck={ this.handleSelect } striped={ true } multiLine={ true } >
                            <Column title="序号" scaleWidth = '4%' textAlign="center">
                                { ( value, index ) => { return Number(index)+1; } }
                            </Column>
                            <Column title="词库名" dataIndex="defineDic" scaleWidth = '22%'textAlign="center"/>
                            <Column title="词库类型" scaleWidth = '10%' textAlign="center">
                                { ( value, index ) => { return <span>{value.type=='dic'?"分词词库":value.type=='synonyms'?"同义词词库":"停用词库"}</span>; } }
                            </Column>
                            <Column title="词库状态"  scaleWidth = '10%' textAlign="center">
                                { ( value, index ) => { return <span>{value.status==1?"启用":"停用"}</span>; } }
                            </Column>
                            <Column title="词库编辑" scaleWidth = '30%' textAlign="center">
                                { ( value, index ) => { 
                                    return (
                                        <div>
                                            <Button type="default" size="tiny" onClick={this.showUpdateModal.bind(this,[value.defineDic,value.type])}><Icon icon="edit" /> 更改名称 </Button>
                                            {
                                                value.status==1?
                                                <Button type="default" size="tiny" onClick={this.showCloseModal.bind(this,[value.defineDic,value.type])}><Icon icon="close" /> 关闭</Button>
                                                :
                                                <Button type="default" size="tiny" onClick={this.showOpenModal.bind(this,[value.defineDic,value.type])}><Icon icon="folder-open" /> 打开</Button>
                                            }
                                            <Button type="default" size="tiny" onClick={this.showDeleteModal.bind(this,[value.defineDic,value.type])}><Icon icon="trash-o" /> 删除</Button>
                                            <Button type="default" size="tiny" onClick={this.dicDetail.bind(this,[value.defineDic,value.type,value.status])}><Icon icon="trash-o" /> 管理</Button>
                                        </div>
                                    )
                                }}
                            </Column>
                        </Table>
                        <Divider />
                        <Pagination index={ index }  total={ total>1000?1000:total } size={ size } align='right' onChange={this.exchangePage}/>
                        <Divider />
                    </Col>
                </Row>
                <Form type="horizontal"
                      action={contextUrl+ "/usou/cluster/" + this.props.page.id + "/_analyze/dic/add"}
                      method="post"
                      async={true}
                      onSubmit={ this.beforeSubmit }
                      onAfterSubmit={this.afterSubmit}
                      trigger={this.formTirggerWord}
                >
                {
                    newVisible?
                    <Modal size="medium" onClose={ this.closeNewModal.bind( this ) }>
                        <ModalHeader>
                            新建词库
                        </ModalHeader>
                        <ModalBody>
                            <FormItem required={true}>
                                <Label>词库类型</Label>
                                <Select name="type" placeholder="请选择词库类型" >
                                    <Option value="dic" >分词词库</Option>
                                    <Option value="synonyms" >同义词词库</Option>
                                    <Option value="stop" >停用词库</Option>
                                </Select>
                            </FormItem>
                            <FormItem required={true}>
                                <Label>词库名称</Label>
                                <Input name="dic" placeholder="请输入词库名称" />
                            </FormItem>
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
                        关闭词库
                    </ModalHeader>
                    <ModalBody height={ 200 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.selectdic }</span>
                        <p>{messageClose}</p>
                        <p>{ mClose }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>
                <Modal visible={ openVisible } onClose={ this.handleOpen_Close.bind( this ) } >
                    <ModalHeader>
                        打开词库
                    </ModalHeader>
                    <ModalBody height={ 200 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.selectdic }</span>
                        <p>{messageOpen}</p>
                        <p>{ mOpen }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleOpen_Close.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>

                <Modal visible={ deleteVisible } onClose={ this.handleDeleteClose.bind( this ) } >
                    <ModalHeader>
                        删除词库
                    </ModalHeader>
                    <ModalBody height={ 300 }>
                        您选择的是：<span style={ { color: 'red' } }>{ this.state.selectdic }</span>
                        <p>{messageDelete}</p>
                        <p>{ mDelete }</p>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleDeleteClose.bind( this ) }>关闭</Button>
                    </ModalFooter>
                </Modal>
              
                <Form type="horizontal"
                    method="post"
                    action={contextUrl+ "/usou/cluster/" + this.props.page.id + "/_analyze/dic/update"}
                    async={true}
                    onSubmit={ this.beforeUpdateSubmit }
                    onAfterSubmit={this.afterUpdateSubmit}
                    trigger={this.formTirggerWordEdit}
                >
                {
                    updateVisible?
                    <Modal size="medium" onClose={ this.closeUpdateModal.bind( this ) } >
                        <ModalHeader>
                            编辑词库
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>类型</Label>
                                <Input name="type" value={`${selecttype=='dic'?'分词词库':selecttype=='synonyms'?"同义词词库":"停用词库"}`} disabled/><br/>
                            </FormItem>
                            <FormItem>
                                <Label>词库名称</Label>
                                <Input name="update" value={ selectdic }/><br/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={ this.closeUpdateModal.bind( this ) }>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                    :
                    null
                }
                </Form>
                <Footer/>
            </Page>
        );
    }
}
indexManage.UIPage = page;
export default indexManage;
