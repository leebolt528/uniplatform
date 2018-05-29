import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,
    DateTime,Dialog,Checkbox,TreeSelect} from 'epm-ui';
/*import { Footer,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';*/

const contextUrl = '/uniplatform';

const page = {
    title: '标签管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css'
    ]
};

class LabelManage extends Component{
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            index_table:1,
            size_table:10,
            total_table:0,
            createBool:true,
            labelHtml: [],
            detailVisible: false,
            importVisible: false,
            keyWord: ''
        }
        this.getFetchLablesList = this.getFetchLablesList.bind(this);
        this.handleValue = this.handleValue.bind(this);
        this.formGetter = this.formGetter.bind(this);
        this.formTirgger = this.formTirgger.bind(this);
        this.handleEditValue = this.handleEditValue.bind(this);
        this.formEditGetter = this.formEditGetter.bind(this);
        this.formEditTirgger = this.formEditTirgger.bind(this);
        this.formUploadTirgger = this.formUploadTirgger.bind(this);
        this.beforeImportSubmit = this.beforeImportSubmit.bind(this);
        this.afterImportSubmit = this.afterImportSubmit.bind(this);
        this.handleSelect = this.handleSelect.bind( this );
    }

    componentDidMount() {
        this.getFetchLablesList();
    }

    //获取标签列表
    getFetchLablesList(){
        let {index,size,keyWord} = this.state;
        let param = new FormData();
        param.append('pageIndex',index);
        param.append('pageSize',size);
        param.append('keyword',keyWord);
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/label/mgmt/labels/list',{
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res)=>res.json())
            .then((data)=>{
                let noData = false;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }else if(data.labels.numbers == 0){
                    noData = true;
                }
                let total = data.labels.numbers;
                let labelsList = [];
                let natureName = data.natureName;
                data.labels.labels.map(function (item) {
                    labelsList.push({
                        labelName : item.name,
                        labelId : item.id,
                        labelStatus : item.status,
                        wordsNumber : item.wordsNumber,
                        createTime : item.createTime,
                        updateTime : item.updateTime
                    });
                });
                this.setState({labelsList,total,natureName,noData});
            }).catch((err)=>console.log(err.toString()));
    }

    //时间戳转换格式
    switchTime(createTime){
        let time = new Date(parseInt(createTime));
        let y = time.getFullYear();
        let m = time.getMonth()+1;
        let d = time.getDate();
        let h = time.getHours();
        let mm = time.getMinutes();
        let s = time.getSeconds();
        return y+'-'+this.format(m)+'-'+this.format(d)+' '+this.format(h)+':'+
            this.format(mm)+':'+this.format(s);
    }
    format(m){
        return m<10?'0'+m:m;
    }

    //标签分页
    exChangePagi(index,size){
        this.setState({index, size}, () => this.getFetchLablesList());
    }

    //表格多选
    handleSelect( data ) {
        this.setState( { selectedLabel: data.map( ( key ) => key.labelId ).join( ',' ),
            selectedStatus: data.map( ( key ) => key.labelStatus )} );
    }

    //获取标签值
    formGetter(getter) {
        this.getValue = getter.value;
    }
    //重置标签
    formTirgger(trigger){
        this.resetLabelName = trigger.reset;
    }
    //点击贴标签
    handleValue() {
        let {labelHtml} = this.state;
        const labelName = this.getValue().replace(/(^\s*)|(\s*$)/g, "");
        if(!labelName){
            popup(<Snackbar message="标签名不能为空" />);
        }else {
            for(let i=0; i<labelHtml.length; i++){
                if(labelHtml[i].key == labelName){
                    popup(<Snackbar message="该标签已存在" />);
                    return;
                }
            }
            labelHtml.push(
                <span key={labelName} onClick={this.removeLabel.bind(this,labelName)} style={{padding:'2px',margin:'2px',backgroundColor:'#cfdef2'}}>
                {labelName}
            </span>
            );
            this.setState({labelHtml});
            this.resetLabelName();
        }
    }
    //移除标签
    removeLabel(labelName){
        let {labelHtml} = this.state;
        labelHtml.map(function (item,index) {
            if(item.key == labelName){
                labelHtml.splice(index,1);
            }
        });
        this.setState({labelHtml});
    }

    //创建标签
    beforeAddLabelSubmit(data){
        let {labelHtml} = this.state;
        let labelNames = '';
        if(labelHtml.length == 0){
            popup(<Snackbar message="至少贴一个标签名"/>);
        }else {
            labelHtml.map(function (item,index) {
                if(labelHtml.length-1 == index){
                    labelNames += item.key;
                }else {
                    labelNames += item.key+',';
                }
            });
            let body = 'names';
            data[body] = labelNames;
            return data;
        }
    }
    afterAddLabelSubmit(data){
        let {labelHtml} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ labelHtml : [],index:1 });
                this.getFetchLablesList();
            }
        }
    }

    //编辑标签回显
    EditLabelClick(labId){
        $(document).scrollTop(0);
        let {labelsList} = this.state;
        let labelEditHtml = [], lableSingleName = [];
        for(let i=0; i<labelsList.length; i++){
            if(labelsList[i].labelId == labId){
                lableSingleName = labelsList[i].labelName.split('_');
            }
        }
        for(let i=0; i<lableSingleName.length; i++){
            labelEditHtml.push(
                <span key={lableSingleName[i]} onClick={this.removeEditClass.bind(this,lableSingleName[i])}
                      style={{padding:'2px',margin:'2px',backgroundColor:'#cfdef2'}}>
                        {lableSingleName[i]}
                    </span>
            );
        }
        this.setState({createBool : false, labelEditHtml, labelId : labId});
    }
    //编辑标签时获取标签名
    formEditGetter(getter) {
        this.getEditValue = getter.value;
    }
    //编辑时重置标签
    formEditTirgger(trigger){
        this.resetEditName = trigger.reset;
    }
    //编辑标签时贴标签
    handleEditValue() {
        let {labelEditHtml} = this.state;
        const labelEditName = this.getEditValue();
        for(let i=0; i<labelEditHtml.length; i++){
            if(labelEditHtml[i].key == labelEditName){
                popup(<Snackbar message="该标签已存在" />);
                return;
            }
        }
        labelEditHtml.push(
            <span key={labelEditName} onClick={this.removeEditClass.bind(this,labelEditName)}
                  style={{padding:'2px',margin:'2px',backgroundColor:'#cfdef2'}}>
                {labelEditName}
            </span>
        );
        this.setState({labelEditHtml});
        this.resetEditName();
    }
    //编辑标签时移除已贴标签
    removeEditClass(labelEditName){
        let {labelEditHtml} = this.state;
        labelEditHtml.map(function (item,index) {
            if(item.key == labelEditName){
                labelEditHtml.splice(index,1);
            }
        });
        this.setState({labelEditHtml});
    }
    //编辑标签
    beforeEditLabelSubmit(data){
        let {labelEditHtml} = this.state;
        let newNames = '';
        if(labelEditHtml.length == 0){
            popup(<Snackbar message="至少贴一个标签名"/>);
        }else {
            labelEditHtml.map(function (item,index) {
                if(labelEditHtml.length-1 == index){
                    newNames += item.key;
                }else {
                    newNames += item.key+',';
                }
            });
            let body = 'newNames';
            data[body] = newNames;
            return data;
        }
    }
    afterEditLabelSubmit(data){
        let {labelEditHtml} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.getFetchLablesList();
                this.setState({ labelEditHtml : [], createBool : true });
            }
        }
    }

    //批量删除
    batchDelete(selectedLabel,selectedStatus){
        this.setState({ message : undefined});
        if(selectedLabel.length == 0){
            popup(<Snackbar message="您未选择任何标签"/>);
        }else if((selectedStatus.length ==1) &&(selectedStatus != 0)){
            popup(<Snackbar message="该标签正在使用中！"/>);
        }else if(selectedStatus.length > 1){
            for(let i=0; i<selectedStatus.length; i++){
                if(selectedStatus[i] != 0){
                    this.setState({ message : "所选标签中正在被使用的标签不能删除，其余已删除成功"});
                }
            }
            this.deleteLabel(selectedLabel);
        }else {
            this.deleteLabel(selectedLabel);
        }
    }
    //删除标签
    deleteLabel(labelId){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleApprove.bind(this,labelId)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该标签?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleApprove(labelId,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/label/mgmt/labels/delete?labelIds='+labelId , {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                let {message} = this.state;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    if (data.status == 200){
                        if(message){
                            popup(<Snackbar message={message}/>);
                        }else {
                            popup(<Snackbar message={data.msg}/>);
                        }
                        this.setState({index : 1});
                        this.getFetchLablesList();
                    }else {
                        popup(<Snackbar message={data.msg}/>);
                    }
                }
                after( true );
            }).catch((err) => {
            console.log(err.toString());
            after( true );
        });
    }

    //从编辑标签中返回到创建标签
    backAddLable(){
        this.setState({createBool : true});
    }

    //导出词
    exportWord(labelId){
        window.open(`${Uniplatform.context.url}/nlap/admin/label/mgmt/label/download?fileName=labelWords&labelId=${labelId}`,'_self');
    }
    //导出标签
    exportLabel(labelId){
        if(labelId.length == 0){
            popup(<Snackbar message="您未选择任何标签"/>);
        }else {
            window.open(`${Uniplatform.context.url}/nlap/admin/label/mgmt/labels/download?fileName=&labelIds=${labelId}`,'_self');
        }
    }

    //标签详情
    showDetailModal(labelId){
        let {index_table,size_table} = this.state;
        let param = new FormData();
        param.append('pageIndex',index_table);
        param.append('pageSize',size_table);
        param.append('keyword','');
        param.append('ascSort',false);
        param.append('labelId',labelId);
        fetch(Uniplatform.context.url + '/nlap/admin/label/mgmt/labels/info',{
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res)=>res.json())
            .then((data)=>{
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let total_table = data.words.wordsNumber;
                let wordsList = [];
                let labelDetailName = data.words.labelName;
                data.words.words.map(function (item) {
                    wordsList.push({
                        wordName : item.wordName,
                        dicName : item.dicName,
                        nature : item.nature
                    });
                });
                this.setState({ labelDetailId : labelId,labelDetailName,wordsList,total_table,detailVisible : true });
            }).catch((err)=>console.log(err.toString()));
    }
    closeDetail(){
        this.setState({ detailVisible : false });
    }

    //标签详情分页
    exChangeWordPagi(labelId,index_table,size_table){
        this.setState({index_table, size_table}, () => this.showDetailModal(labelId));
    }

    //导入标签
    importLabelModal(){
        this.setState({ importVisible : true });
    }
    closeImport(){
        this.resetUpload();
        this.setState({ importVisible : false });
    }
    beforeImportSubmit(data){
        for(let i=0; i<data.files.length; i++){
            let suffix = data.files[i].name.split('.');
            if(suffix[suffix.length-1] != 'txt'){
                popup(<Snackbar message="文件只能是.txt格式的文件"/>);
                return false;
            }
        }
        if(data.files.length == 0){
            popup(<Snackbar message="文件不能为空" />);
            return false;
        }else {
            return true;
        }
    }
    formUploadTirgger(trigger){
        this.resetUpload = trigger.reset;
    }
    afterImportSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ importVisible : false });
                this.resetUpload();
                this.getFetchLablesList();
            }
        }
    }

    //词列表词性 中文显示
    getNature(data){
        let result = '';
        let { natureName } = this.state;
        Object.keys(natureName).map(function (item) {
            if(item == data){
                result = natureName[item];
            }
        });
        return result;
    }

    //标签检索
    labelSearch(event){
        if (event.which==13){
            let keyWord = this.getLabelValue();
            this.setState({keyWord}, () => this.getFetchLablesList());
        }
    }
    getLabelInput(getter){
        this.getLabelValue=getter.value;
    }
    wordSearchAuto(data){
        if(data == ''){
            this.setState({ keyWord : '' }, () => this.getFetchLablesList());
        }
    }

    render(){
        let {index,size,total,createBool,labelHtml,labelsList,labelEditHtml,labelId,detailVisible,importVisible,
            selectedLabel,labelDetailName,wordsList,total_table,index_table,size_table,labelDetailId,selectedStatus,
            noData}=this.state;
        return(
            <Page>
                {/*<COMM_HeadBanner prefix=" " />*/}
                {/*<div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px',margin:'0px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'910px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <NLPMenu url={'/nlap/platform/labelManage'}/>
                    </Col>*/}
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0',marginTop:'20px'}}>
                        {createBool ?
                            <Row className="dicManage_row">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                    <div className = "dicManage_title" >
                                        <span className = "dicManage_span">创建标签</span>
                                    </div>
                                </Col>
                                <div className="dicManage_content">
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'0 2%',padding:'2%',width:'95%'}}>
                                        <Form type="inline"
                                              method="post"
                                              action={contextUrl + '/nlap/admin/label/mgmt/label/add'}
                                              async={true}
                                              onSubmit={this.beforeAddLabelSubmit.bind(this)}
                                              onAfterSubmit={this.afterAddLabelSubmit.bind(this)}
                                        >
                                            <FormItem >
                                                <Label>标签：</Label>
                                                <Input placeholder="请输入标签名" getter={this.formGetter} trigger={this.formTirgger}/>
                                            </FormItem>
                                            <Button onClick={this.handleValue}>添加</Button>
                                            <div style={{border:'1px dashed #d8dde6',borderRadius:'5px',width:'600px',height:'100px',marginLeft:'54px',overflow:'auto'}}>
                                                {labelHtml}
                                            </div>
                                            <Divider/>
                                            <Button type="primary" htmlType="submit" style={{marginLeft:'50%'}}>确认创建</Button>
                                        </Form>
                                    </Col>
                                </div>
                            </Row>
                            :
                            ''
                        }
                        {createBool ? '' :
                            <Row className="dicManage_row">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col" style={{borderBottom:'1px solid red'}}>
                                    <div className = "dicManage_title" >
                                        <span className = "dicManage_span">编辑标签</span>
                                    </div>
                                </Col>
                                <div className="dicManage_content" style={{border:'1px solid red'}}>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'0 2%',padding:'2%',width:'95%'}}>
                                        <Form type="inline"
                                              method="post"
                                              action={contextUrl + '/nlap/admin/label/mgmt/label/edit'}
                                              async={true}
                                              onSubmit={this.beforeEditLabelSubmit.bind(this)}
                                              onAfterSubmit={this.afterEditLabelSubmit.bind(this)}
                                        >
                                            <Input name="labelId" value={labelId} style={{display:'none'}}/>
                                            <FormItem >
                                                <Label>标签：</Label>
                                                <Input placeholder="请输入标签名" getter={this.formEditGetter} trigger={this.formEditTirgger} style={{display:'inline-block'}}/>
                                            </FormItem>
                                            <Button onClick={this.handleEditValue}>添加</Button>
                                            <div style={{border:'1px dashed #d8dde6',borderRadius:'5px',width:'600px',height:'100px',marginLeft:'54px',overflow:'auto'}}>
                                                {labelEditHtml}
                                            </div>
                                            <Divider/>
                                            <Button type="primary" htmlType="submit" style={{marginLeft:'45%'}}>保存修改</Button>
                                            <Button type="default" onClick={this.backAddLable.bind(this)}>取消</Button>
                                        </Form>
                                    </Col>
                                </div>
                            </Row>
                        }
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">标签管理</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 5, small: 24, medium: 5, large: 5 }} style={{position: 'relative',zIndex: '1',marginTop:'20px'}}>
                                    <Input placeholder="请输入要查询的关键字" type="search"
                                           onKeyPress={ this.labelSearch.bind(this)}
                                           getter={ this.getLabelInput.bind(this) }
                                           onChange={this.wordSearchAuto.bind(this)}>
                                        <Input.Left icon="search"/>
                                    </Input>
                                </Col>
                                <ButtonGroup style={{float:'right',margin:'15px'}}>
                                    <Button onClick={this.importLabelModal.bind(this)}><i className="fa fa-upload"></i> 导入标签</Button>
                                    {noData ?
                                        <Button disabled><i className="fa fa-download"></i> 导出标签</Button>
                                        :
                                        <Button onClick={this.exportLabel.bind(this,selectedLabel)}><i className="fa fa-download"></i> 导出标签</Button>
                                    }
                                    {noData ?
                                        <Button disabled><i className="fa fa-trash"></i> 批量删除</Button>
                                        :
                                        <Button onClick={this.batchDelete.bind(this,selectedLabel,selectedStatus)}><i className="fa fa-trash"></i> 批量删除</Button>
                                    }
                                </ButtonGroup>

                                {noData ?
                                    <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                        暂无数据
                                    </div>
                                    :
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                        <Table  striped={true} checkable complex headBolder={ true }
                                                onCheck={ this.handleSelect } dataSource={labelsList}>
                                            <Column title="序号" scaleWidth = '6%' textAlign="center">
                                                { ( value, index ) => { return Number(`${Number(this.state.index-1)*Number(this.state.size)+Number(index)}`) + 1; } }
                                            </Column>
                                            <Column dataIndex="labelName" title="标签名称" scaleWidth='20%' textAlign="center"/>
                                            <Column dataIndex="wordsNumber" title="词汇量" scaleWidth='10%' textAlign="center"/>
                                            <Column title="状态" scaleWidth='10%' textAlign="center">
                                                {(value)=>{
                                                    let status = '';
                                                    switch (value.labelStatus){
                                                        case 0:
                                                            status = '未使用';
                                                            break;
                                                        case 1:
                                                            status = '使用中';
                                                            break;
                                                        default:
                                                            status = '';
                                                            break;
                                                    }
                                                    return status;
                                                }}
                                            </Column>
                                            <Column title="创建时间" scaleWidth='17%' textAlign="center">
                                                {(value)=>{
                                                    return this.switchTime(value.createTime);
                                                }}
                                            </Column>
                                            <Column title="更新时间" scaleWidth='17%' textAlign="center">
                                                {(value)=>{
                                                    return value.updateTime ? this.switchTime(value.updateTime) : '';
                                                }}
                                            </Column>
                                            <Column title="操作" scaleWidth='20%' textAlign="center" >
                                                {( value )=>{
                                                    return(
                                                        <div>
                                                            {value.labelStatus ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    <i className="edit"></i> 编辑
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.EditLabelClick.bind(this,value.labelId)}>
                                                                    <i className="edit"></i> 编辑
                                                                </Button>
                                                            }
                                                            {value.labelStatus ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    <i className="trash-o"></i> 删除
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.deleteLabel.bind(this,value.labelId)}>
                                                                    <i className="trash-o"></i> 删除
                                                                </Button>
                                                            }
                                                            {value.wordsNumber == 0 ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    <i className="trash-o"></i> 导出词
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.exportWord.bind(this,value.labelId)}>
                                                                    <i className="trash-o"></i> 导出词
                                                                </Button>
                                                            }
                                                            {value.wordsNumber == 0 ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    <i className="trash-o"></i> 详情
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.showDetailModal.bind(this,value.labelId)}>
                                                                    <i className="trash-o"></i> 详情
                                                                </Button>
                                                            }

                                                        </div>
                                                    )
                                                }}
                                            </Column>
                                        </Table>
                                        <Divider/>
                                        <Pagination index={index} total={total} size={size} onChange={this.exChangePagi.bind(this)}
                                                    align='center' showDataSizePicker={true} dataSizePickerList={['10','20','50','100']}/>
                                        <Divider/>
                                    </Col>
                                }

                            </div>
                        </Row>
                    </Col>
                </Row>

                {detailVisible ?
                    <Modal size="medium" onClose={this.closeDetail.bind(this)}>
                        <ModalHeader>
                            标签详情
                        </ModalHeader>
                        <ModalBody>
                            <Col style={{margin:'0 20px'}}>
                                <p><span style={{fontWeight:'600'}}>标签名：</span>{labelDetailName}</p>
                                <Divider/>
                                <Table  striped={true} complex headBolder={ true } bordered dataSource={wordsList}>
                                    <Column title="序号" scaleWidth = '5%' textAlign="center">
                                        { ( value, index ) => { return Number(`${Number(this.state.index_table-1)*Number(this.state.size_table)+Number(index)}`) + 1; } }
                                    </Column>
                                    <Column dataIndex="wordName" title="词名称" scaleWidth='30%' textAlign="center"/>
                                    <Column title="词性" scaleWidth='30%' textAlign="center">
                                        { ( value ) => {
                                            let result = this.getNature(value.nature);
                                            return result;
                                        } }
                                    </Column>
                                    <Column dataIndex="dicName" title="所属词库" scaleWidth='30%' textAlign="center"/>
                                </Table>
                                <Divider/>
                                <Pagination index={index_table} total={total_table} size={size_table} onChange={this.exChangeWordPagi.bind(this,labelDetailId)} align='center'/>
                                <Divider/>
                            </Col>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeDetail.bind(this)}>关闭</Button>
                        </ModalFooter>
                    </Modal>
                    : ''
                }


                <Form
                      method="post"
                      action={contextUrl + '/nlap/admin/label/mgmt/labels/upload'}
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={ this.beforeImportSubmit }
                      onAfterSubmit={this.afterImportSubmit}
                >
                    <Modal visible={ importVisible } size="medium" onClose={ this.closeImport.bind( this ) }>
                        <ModalHeader>
                            导入标签
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>文件上传</Label>
                                <Upload placeholder="上传文件" name="files" multiple={true} trigger={ this.formUploadTirgger } style={{display:'inline-block'}}/>
                            </FormItem>
                            <Divider/>
                            <span style={{ float:"left",textAlign:"left",marginLeft:"40px"}}> 1、 系统支持.txt格式的文件进行上传。<br/>
                                    2、.txt文件的<a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=label.txt">示例文件</a>
                            </span>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeImport.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>


                {/*<Footer />*/}
            </Page>
        );
    };
}

LabelManage.UIPage = page;
export default LabelManage;