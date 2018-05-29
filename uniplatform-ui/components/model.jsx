import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,popup,Snackbar,Pagination,Dialog,Upload,RadioGroup,Radio} from 'epm-ui';
/*import { Footer,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';*/

const contextUrl = '/uniplatform';

const page = {
    title: '模型管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css'
    ]
};

class ModelManage extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            addModelVisible: false,
            editVisible: false,
            dataSetList: [],
            showAlgorith: false,
            importVisible: false
        }
        this.getFetchFunctionsList = this.getFetchFunctionsList.bind(this);
        this.getFetchModelsList = this.getFetchModelsList.bind(this);
        this.handleModelSelect = this.handleModelSelect.bind(this);
        this.formAddTirgger = this.formAddTirgger.bind(this);
        this.formEditTirgger = this.formEditTirgger.bind(this);
        this.afterAddSubmit = this.afterAddSubmit.bind(this);
        this.afterEditSubmit = this.afterEditSubmit.bind(this);
        this.beforeAddSubmit = this.beforeAddSubmit.bind(this);
        this.beforeEditSubmit = this.beforeEditSubmit.bind(this);
        this.formUploadTirgger = this.formUploadTirgger.bind(this);
        this.formFileTirgger = this.formFileTirgger.bind(this);
        this.beforeImportSubmit = this.beforeImportSubmit.bind(this);
        this.afterImportSubmit = this.afterImportSubmit.bind(this);
    }

    componentDidMount() {
        this.getFetchFunctionsList();
    }

    //获取分类（功能）与算法
    getFetchFunctionsList(){
        fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/functions/algorithms/list', {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                let {funcName} = this.state;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let algorithmsList = data.functions;
                this.setState({algorithmsList});
                this.showFunctions();
                if(funcName){
                    this.handleFunctionClick(funcName);
                }
            }).catch((err) => console.log(err.toString()));
    }

    //展示分类（功能）
    showFunctions(){
        let {algorithmsList} = this.state;
        let functionHtml = [];
        for(let i=0; i<algorithmsList.length; i++){
            functionHtml.push(
                <a onClick={this.handleFunctionClick.bind(this,algorithmsList[i].functionName)} key={i}>
                    <div style={{display:'inline-block',border:'1px solid #d8dde8',margin:'1.5%',borderRadius:'3px',width:'22%',height:'100px'}}>
                        <Row style={{color:'#0070d2',fontSize:'18px',marginTop:'8%'}}>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }} style={{textAlign:'right'}}>功能名称:</Col>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>{algorithmsList[i].functionName}</Col>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }} style={{textAlign:'right'}}>模型数量:</Col>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>{algorithmsList[i].modelsNumber}</Col>
                        </Row>
                    </div>
                </a>
            );
        }
        this.setState({functionHtml});
    }

    //处理点击算法操作
    handleOnClickAlgo(functionId,algorithmId,loc){
        let algo_style = document.getElementsByClassName('algo_select');
        for(let i=0; i<algo_style.length; i++){
            algo_style[i].style.backgroundColor = null;
            algo_style[i].style.border = 'solid 1px #d8dde8';
        }
        algo_style[loc].style.backgroundColor = '#ceeefc';
        algo_style[loc].style.border = "solid 1px #6fccf5";

        let {algorithmsList} = this.state;
        let algorithmName = '';
        for(let i=0;i<algorithmsList.length;i++){
            let al = algorithmsList[i].algorithmsList;
            for(let j=0;j<al.length;j++){
                if(al[j].id == algorithmId){
                    algorithmName = al[j].displayName;
                }
            }
        }
        this.setState({functionId,algorithmId,algorithmName});
        if(algorithmId == 'all'){
            this.getFetchModelsList(functionId,'');
        }else{
            this.getFetchModelsList('',algorithmId);
        }
    }
    //获取模型列表
    getFetchModelsList(functionId,algorithmId){
        let param = new FormData();
        let {index,size} = this.state;
        param.append('pageIndex', index);
        param.append('pageSize', size);
        param.append('keyword','');
        param.append('functionId',functionId);
        param.append('algorithmId',algorithmId);
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/models', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let noData = false;
                if(data.models[0].modelInfo.length == 0){
                    noData = true;
                }
                let modelsList = [];
                let total = data.models[0].modelNumbers;
                data.models[0].modelInfo.map(function (item) {
                    modelsList.push({
                        modelId : item.modelInfo.id,
                        modelName : item.modelInfo.name,
                        dataSetName : item.dataSetName,
                        dataSetId : item.modelInfo.dataSetId,
                        algorithmName : item.algorithmName,
                        algorithmId : item.modelInfo.algorithmId,
                        createTime : item.modelInfo.createTime,
                        trainedTime : item.modelInfo.trainedTime,
                        functionId : item.modelInfo.functionId,
                        inUsing : item.modelInfo.inUsing,
                        modelStatus : item.modelInfo.modelStatus,
                        operation : item.modelInfo.operation
                    });
                });
                this.setState({modelsList,total,noData});
            }).catch((err) => console.log(err.toString()));

        this.timer && clearTimeout(this.timer);
        this.timer = setTimeout( () => {
            this.getFetchModelsList(functionId,algorithmId);
        }, 5000 );
    }

    //获取数据集列表
    getFetchDataSet(){
        let {dataSetList,functionId} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/dataSet/list?functionId='+functionId, {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                dataSetList = data.models.map(function(item){
                    let ds = {
                        value: item.id,
                        text: item.name
                    }
                    return ds;
                });
                this.setState({dataSetList});
            }).catch((err) => console.log(err.toString()));
    }

    //分页
    exChangePagi(index,size){
        let {functionId,algorithmId} = this.state;
        if(algorithmId == 'all'){
            this.setState({index, size}, () => this.getFetchModelsList(functionId,''));
        }else{
            this.setState({index, size}, () => this.getFetchModelsList('',algorithmId));
        }

    }

    //时间戳转换
    switchTime(createTime){
        if(createTime){
            let time = new Date(parseInt(createTime));
            let y = time.getFullYear();
            let m = time.getMonth()+1;
            let d = time.getDate();
            let h = time.getHours();
            let mm = time.getMinutes();
            let s = time.getSeconds();
            return y+'-'+this.format(m)+'-'+this.format(d)+' '+this.format(h)+':'+
                this.format(mm)+':'+this.format(s);
        }else {
            return '';
        }
    }
    format(m){
        return m<10?'0'+m:m;
    }

    //表格多选
    handleModelSelect( data ) {
        this.setState( { selectedModel: data.map( ( key ) => key.modelId ).join( ',' ),
            selectedInUsing: data.map( ( key ) => key.inUsing ), selectedOperation: data.map( ( key ) => key.operation ),
            selectedStatus: data.map( ( key ) => key.modelStatus )} );
    }
    //删除模型
    deleteModel(model,selectedInUsing,selectedOperation,selectedStatus){
        this.setState({ message : undefined });
        if(model.length == 0){
            popup(<Snackbar message="您未选择任何模型"/>);
        }else if(selectedInUsing.length == 1){
            if(selectedInUsing[0]){
                popup(<Snackbar message="该模型正在被使用，不能删除"/>);
            }else {
                if(selectedOperation[0] != 'custom'){
                    popup(<Snackbar message="该模型为非自定义模型，不能删除"/>);
                }else {
                    if((selectedStatus[0] == 1) || (selectedStatus[0] == 3)){
                        popup(<Snackbar message="该模型正在训练，不能删除"/>);
                    }else {
                        this.delModel(model);
                    }
                }
            }
        }else {
            for(let i=0; i<selectedInUsing.length; i++){
                if(selectedInUsing[i]){
                    this.setState({ message : "所选模型中正在被使用的模型不能删除，其余已删除成功"});
                }else {
                    if(selectedOperation[i] != 'custom'){
                        this.setState({ message : "所选模型中非自定义模型不能删除，其余已删除成功"});
                    }else {
                        if((selectedStatus[i] == 1) || (selectedStatus[i] == 3)){
                            this.setState({ message : "所选模型中正在训练的模型不能删除，其余已删除成功"});
                        }
                    }
                }
            }
            this.delModel(model);
        }
    }
    //删除模型
    delModel(model){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleApprove.bind(this,model)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该模型?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleApprove(model,after) {
        let {functionId,algorithmId} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/model/delete?modelsId='+model , {
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
                        this.getFetchFunctionsList();
                        if(algorithmId == 'all'){
                            this.getFetchModelsList(functionId,'');
                        }else {
                            this.getFetchModelsList('',algorithmId);
                        }
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

    //添加模型
    addModal(){
        this.getAlgorithmList();
        this.getFetchDataSet();
        this.setState({ addModelVisible : true });
    }
    closeAdd(){
        this.resetAdd();
        this.setState({ addModelVisible : false });
    }
    formAddTirgger(trigger){
        this.resetAdd = trigger.reset;
    }
    beforeAddSubmit(data){
        let modelName = data.modelName.replace(/(^\s*)|(\s*$)/g, "");
        if(!modelName){
            popup(<Snackbar message="模型名称不能为空"/>);
            return false;
        }else if(!data.dataSetId){
            popup(<Snackbar message="数据集不能为空"/>);
            return false;
        }else {
            let m = 'modelName';
            data[m] = modelName;
            return data;
        }
    }
    afterAddSubmit(data){
        let {functionId,algorithmId} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.getFetchFunctionsList();
                this.setState({ addModelVisible : false });
                this.resetAdd();
                this.getFetchModelsList(functionId,algorithmId);
            }
        }
    }

    //编辑模型
    editModal(model){
        this.getFetchDataSet();
        let {modelsList} = this.state;
        let modelId,modelName,dataSetId,algorithmId_echo;
        modelsList.map(function (item) {
            if(item.modelId == model){
                modelId = item.modelId;
                modelName = item.modelName;
                dataSetId = item.dataSetId;
                algorithmId_echo = item.algorithmId;
            }
        });
        this.resetEdit();
        this.setState({ modelId,modelName,dataSetId,algorithmId_echo,editVisible : true });
    }
    beforeEditSubmit(data){
        let newModelName = data.newModelName.replace(/(^\s*)|(\s*$)/g, "");
        if(!newModelName){
            popup(<Snackbar message="模型名称不能为空"/>);
            return false;
        }else {
            let n = 'newModelName';
            data[n] = newModelName;
            return data;
        }
    }
    afterEditSubmit(data){
        let {functionId,algorithmId_echo} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ editVisible : false });
                this.getFetchModelsList(functionId,algorithmId_echo);
            }
        }
    }
    closeEdit(){
        this.setState({ editVisible : false });
    }
    formEditTirgger(trigger){
        this.resetEdit = trigger.reset;
    }

    //点击功能后切换为展示算法
    handleFunctionClick(funcName){
        let {algorithmsList} = this.state;
        let algorithmHtml = [];
        let functionId = '';
        for(let i=0; i<algorithmsList.length; i++){
            if(algorithmsList[i].functionName == funcName){
                let algo = algorithmsList[i].algorithmsList;
                for(let j=0; j<algo.length; j++){
                    functionId = algo[j].functionId;
                    algorithmHtml.push(
                        <a onClick={this.handleOnClickAlgo.bind(this,algo[j].functionId,algo[j].id,j)} key={j}>
                            <div className="algo_select" style={{display:'inline-block',border:'1px solid #d8dde8',margin:'1%',borderRadius:'3px',width:'14.5%',height:'100px'}}>
                                <Row style={{color:'#0070d2',fontSize:'18px',marginTop:'8%',textAlign:'center'}}>
                                    <span>{algo[j].displayName}</span>
                                    <Divider/>
                                    <span>（{algo[j].modelsNum}）</span>
                                </Row>
                            </div>
                        </a>
                    );
                }
            }
        }
        this.setState({functionId,algorithmHtml,showAlgorith : true,funcName});
    }

    //点击返回切回功能
    backFunction(){
        let algo_style = document.getElementsByClassName('algo_select');
        for(let i=0; i<algo_style.length; i++){
            algo_style[i].style.backgroundColor = null;
            algo_style[i].style.border = 'solid 1px #d8dde8';
        }
        this.setState({showAlgorith : false, modelsList : undefined});
    }

    //批量导出
    batchExport(selectedModel,selectedStatus){
        if(selectedModel.length == 0){
            popup(<Snackbar message="您未选择任何模型"/>);
        }else if(selectedStatus.length == 1){
            if((selectedStatus[0] == 1) || (selectedStatus[0] == 0)){
                popup(<Snackbar message="该模型不符合导出条件"/>);
            }else {
                this.export(selectedModel);
            }
        }else {
            let noload = [];
            for(let i=0; i<selectedStatus.length; i++){
                if((selectedStatus[i] == 1) || (selectedStatus[i] == 0)){
                    noload.push(selectedStatus[i]);
                }
            }
            if(noload.length == selectedStatus.length){
                popup(<Snackbar message="没有可下载的模型"/>);
                return;
            }else {
                popup(<Snackbar message="所选模型中有模型不符合导出条件，其余导出成功"/>);
            }
            this.export(selectedModel);
        }
    }

    //导出模型
    export(selectedModel){
        window.open(`${Uniplatform.context.url}/nlap/admin/model/mgmt/model/download?fileName=models&modelIds=${selectedModel}`,'_self');

        /*let url = `${Uniplatform.context.url}/nlap/admin/model/mgmt/model/download?fileName=models&modelIds=${selectedModel}`;
        const form = document.createElement('form');
        form.target = '_new';
        form.method = 'post';
        form.action = url;
        document.body.appendChild(form);
        form.submit();
        console.log(form.data);
        document.body.removeChild(form);*/

    }

    //导入模型
    importModal(){
        this.getAlgorithmList();
        this.setState({ importVisible : true });
    }
    closeImport(){
        this.resetUpload();
        this.resetFile();
        this.setState({ importVisible : false });
    }
    beforeImportSubmit(data){
        let modelName = data.modelName.replace(/(^\s*)|(\s*$)/g, "");
        if(!modelName){
            popup(<Snackbar message="模型名称不能为空" />);
            return false;
        }else if(!data.algorithmId){
            popup(<Snackbar message="算法不能为空" />);
            return false;
        }else if(data.files.length == 0){
            popup(<Snackbar message="文件不能为空" />);
            return false;
        }else {
            let m = 'modelName';
            data[m] = modelName;
            return data;
        }
    }
    formUploadTirgger(trigger){
        this.resetUpload = trigger.reset;
    }
    formFileTirgger(trigger){
        this.resetFile = trigger.reset;
    }
    afterImportSubmit(data){
        let {functionId,algorithmId} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.getFetchFunctionsList();
                this.setState({ importVisible : false });
                this.resetUpload();
                this.resetFile();
                if(algorithmId == 'all'){
                    this.getFetchModelsList(functionId,'');
                }else {
                    this.getFetchModelsList('',algorithmId);
                }

            }
        }
    }

    //训练模型
    train(modelId,modelStatus){
        if(modelStatus == 2){
            const cancelBtn = { text: "否" };
            const approveBtn = {
                text: "是",
                type: "warning",
                onClick: this.trainModel.bind(this,modelId)
            };
            popup(<Dialog style={{top:'15%'}}
                          message="该模型已经训练完成，是否重新训练?"
                          type="alert"
                          showCancelBtn
                          cancelBtn={ cancelBtn }
                          showApproveBtn
                          approveBtn = { approveBtn }
            />);
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/model/train?modelId='+modelId, {
                credentials: 'same-origin',
                method: 'POST'
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else {
                        popup(<Snackbar message={data.msg}/>);
                    }
                }).catch((err) => console.log(err.toString()));
        }
    }
    trainModel(modelId,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/model/train?modelId='+modelId, {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    popup(<Snackbar message={data.msg}/>);
                }
                after( true );
            }).catch((err) => {
            console.log(err.toString());
            after( true );
        });
    }

    //算法列表
    getAlgorithmList(){
        let {algorithmsList,funcName} = this.state;
        let algo = [], algorithmList = [];
        algorithmsList.map(function (item) {
            if(item.functionName == funcName){
                item.algorithmsList.map(function (al) {
                    algo.push(al);
                });
            }
        });
        for(let i=1; i<algo.length; i++){
            algorithmList.push({
                text : algo[i].displayName,
                value : algo[i].id
            });
        }
        this.setState({algorithmList});
    }

    //应用
    application(modelId){
        fetch(Uniplatform.context.url + '/nlap/admin/model/mgmt/model/apply?modelId='+modelId, {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    popup(<Snackbar message={data.msg}/>);
                }
            }).catch((err) => console.log(err.toString()));
    }

    reapplication(modelId){
        const cancelBtn = { text: "否" };
        const approveBtn = {
            text: "是",
            type: "warning",
            onClick: this.application.bind(this,modelId)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="该模型已经下发成功，是否重新下发?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }

    render() {
        let {functionHtml,algorithmHtml,showAlgorith,modelsList,selectedModel,index,size,total,addModelVisible,
            dataSetList,algorithmName,algorithmId,functionId,editVisible,modelId,modelName,dataSetId,
            algorithmId_echo,importVisible,selectedInUsing,selectedOperation,selectedStatus,algorithmList,noData} = this.state;
        return (
            <Page>
                {/*<COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <NLPMenu url={'/nlap/platform/model'}/>
                        </div>
                    </Col>*/}
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">模型管理
                                        {showAlgorith ?
                                            <a style={{float:'right'}} onClick={this.backFunction.bind(this)}>
                                                <i className="fa fa-undo"></i> 返回
                                            </a>
                                            : ''
                                        }
                                    </span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                    {showAlgorith ? algorithmHtml : functionHtml}
                                </Col>
                            </div>
                        </Row>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">模型列表</span>
                                </div>
                            </Col>
                            {modelsList ?
                                <div className="dicManage_content">
                                    <ButtonGroup style={{float:'right',margin:'15px'}}>
                                        <Button onClick={this.addModal.bind(this)}><i className="fa fa-plus"></i> 新增模型</Button>
                                        <Button onClick={this.importModal.bind(this)}><i className="fa fa-upload"></i> 导入模型</Button>
                                        {noData ?
                                            <Button disabled><i className="fa fa-download"></i> 导出模型</Button>
                                            :
                                            <Button onClick={this.batchExport.bind(this,selectedModel,selectedStatus)}><i className="fa fa-download"></i> 导出模型</Button>
                                        }
                                        {noData ?
                                            <Button disabled><i className="fa fa-trash"></i> 批量删除</Button>
                                            :
                                            <Button onClick={this.deleteModel.bind(this,selectedModel,selectedInUsing,selectedOperation,selectedStatus)}><i className="fa fa-trash"></i> 批量删除</Button>
                                        }
                                    </ButtonGroup>

                                    {noData ?
                                        <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                            暂无数据
                                        </div>
                                        :
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                            <Table  striped={true} checkable headBolder={ true } complex
                                                    onCheck={ this.handleModelSelect } dataSource={modelsList}>
                                                <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                    { ( value, index ) => { return Number(`${Number(this.state.index-1)*Number(this.state.size)+Number(index)}`) + 1; } }
                                                </Column>
                                                <Column dataIndex="modelName" title="模型名称" scaleWidth='10%' textAlign="center"/>
                                                <Column dataIndex="algorithmName" title="算法" scaleWidth='10%' textAlign="center"/>
                                                <Column dataIndex="dataSetName" title="数据集" scaleWidth='10%' textAlign="center"/>
                                                <Column title="创建时间" scaleWidth='15%' textAlign="center">
                                                    {(value)=>{
                                                        return this.switchTime(value.createTime);
                                                    }}
                                                </Column>
                                                <Column title="训练时间" scaleWidth='15%' textAlign="center">
                                                    {(value)=>{
                                                        return this.switchTime(value.trainedTime);
                                                    }}
                                                </Column>
                                                <Column title="状态" scaleWidth='10%' textAlign="center">
                                                    {(value)=>{
                                                        let status;
                                                        switch (value.modelStatus) {
                                                            case 0:
                                                                status = '待训练';
                                                                break;
                                                            case 1:
                                                                status = '训练中';
                                                                break;
                                                            case 2:
                                                                status = '训练完成';
                                                                break;
                                                            case 3:
                                                                status = '下发中';
                                                                break;
                                                            case 4:
                                                                status = '已下发';
                                                                break;
                                                            case 5:
                                                                status = '下发失败';
                                                                break;
                                                            case 6:
                                                                status = '训练失败';
                                                                break;
                                                            default:
                                                                status = '';
                                                                break;
                                                        }
                                                        return status;
                                                    }}
                                                </Column>
                                                <Column title="操作" scaleWidth='25%' textAlign="center" >
                                                    {( value )=>{
                                                        return(
                                                            <div>
                                                                {value.inUsing ?
                                                                    <div>
                                                                        <Button size="tiny" disabled>编辑</Button>
                                                                        <Button size="tiny" disabled>删除</Button>
                                                                        <Button size="tiny" disabled>训练</Button>
                                                                        {(value.modelStatus == 2) || (value.modelStatus == 4) ?
                                                                            <Button size="tiny" onClick={this.export.bind(this,value.modelId)}>导出</Button>
                                                                            :
                                                                            <Button size="tiny" disabled>导出</Button>
                                                                        }
                                                                    </div>
                                                                    :
                                                                    (value.operation == 'system' ?
                                                                            <div>
                                                                                <Button size="tiny" disabled>编辑</Button>
                                                                                <Button size="tiny" disabled>删除</Button>
                                                                                <Button size="tiny" onClick={this.train.bind(this,value.modelId,value.modelStatus)}>训练</Button>
                                                                                <Button size="tiny" onClick={this.export.bind(this,value.modelId)}>导出</Button>
                                                                            </div>
                                                                            :
                                                                            (value.operation == 'custom_upload' ?
                                                                                    (value.modelStatus == 3 ?
                                                                                            <div>
                                                                                                <Button size="tiny" disabled>编辑</Button>
                                                                                                <Button size="tiny" disabled>删除</Button>
                                                                                                <Button size="tiny" disabled>训练</Button>
                                                                                                <Button size="tiny" disabled>导出</Button>
                                                                                                <Button size="tiny" disabled>应用</Button>
                                                                                            </div>
                                                                                            :
                                                                                            <div>
                                                                                                <Button size="tiny" disabled>编辑</Button>
                                                                                                <Button size="tiny" onClick={this.deleteModel.bind(this,value.modelId)}>删除</Button>
                                                                                                <Button size="tiny" disabled>训练</Button>
                                                                                                <Button size="tiny" onClick={this.export.bind(this,value.modelId)}>导出</Button>
                                                                                                <Button size="tiny" onClick={this.reapplication.bind(this,value.modelId)}>应用</Button>
                                                                                            </div>
                                                                                    )
                                                                                    :
                                                                                    (value.modelStatus == 1 ?
                                                                                            <div>
                                                                                                <Button size="tiny" disabled>编辑</Button>
                                                                                                <Button size="tiny" disabled>删除</Button>
                                                                                                <Button size="tiny" disabled>训练</Button>
                                                                                                <Button size="tiny" disabled>导出</Button>
                                                                                            </div>
                                                                                            :
                                                                                            (value.modelStatus == 3 ?
                                                                                                    <div>
                                                                                                        <Button size="tiny" disabled>编辑</Button>
                                                                                                        <Button size="tiny" disabled>删除</Button>
                                                                                                        <Button size="tiny" disabled>训练</Button>
                                                                                                        <Button size="tiny" onClick={this.export.bind(this,value.modelId)}>导出</Button>
                                                                                                    </div>
                                                                                                    :
                                                                                                    (value.modelStatus == 0 ?
                                                                                                            <div>
                                                                                                                <Button size="tiny" onClick={this.editModal.bind(this,value.modelId)}>编辑</Button>
                                                                                                                <Button size="tiny" onClick={this.deleteModel.bind(this,value.modelId)}>删除</Button>
                                                                                                                <Button size="tiny" onClick={this.train.bind(this,value.modelId,value.modelStatus)}>训练</Button>
                                                                                                                <Button size="tiny" disabled>导出</Button>
                                                                                                            </div>
                                                                                                            :
                                                                                                            (value.modelStatus == 4 ?
                                                                                                                    <div>
                                                                                                                        <Button size="tiny" onClick={this.editModal.bind(this,value.modelId)}>编辑</Button>
                                                                                                                        <Button size="tiny" onClick={this.deleteModel.bind(this,value.modelId)}>删除</Button>
                                                                                                                        <Button size="tiny" disabled>训练</Button>
                                                                                                                        <Button size="tiny" onClick={this.export.bind(this,value.modelId)}>导出</Button>
                                                                                                                    </div>
                                                                                                                    :
                                                                                                                    <div>
                                                                                                                        <Button size="tiny" onClick={this.editModal.bind(this,value.modelId)}>编辑</Button>
                                                                                                                        <Button size="tiny" onClick={this.deleteModel.bind(this,value.modelId)}>删除</Button>
                                                                                                                        <Button size="tiny" onClick={this.train.bind(this,value.modelId,value.modelStatus)}>训练</Button>
                                                                                                                        <Button size="tiny" onClick={this.export.bind(this,value.modelId)}>导出</Button>
                                                                                                                    </div>
                                                                                                            )
                                                                                                    )
                                                                                            )

                                                                                    )
                                                                            )
                                                                    )

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
                                : ''
                            }

                        </Row>
                    </Col>
                </Row>


                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/model/mgmt/model/add'}
                      trigger={ this.formAddTirgger }
                      async={true}
                      onSubmit={this.beforeAddSubmit}
                      onAfterSubmit={this.afterAddSubmit}
                >
                    <Modal visible={ addModelVisible } size="medium" onClose={ this.closeAdd.bind( this ) }>
                        <ModalHeader>
                            新建模型
                        </ModalHeader>
                        <ModalBody>
                            <Input name="functionId" value={functionId} style={{display:'none'}} />
                            <Input name="algorithmId" value={algorithmId} style={{display:'none'}} />
                            {algorithmName == '全部' ?
                                <FormItem>
                                    <Label>算法名称</Label>
                                    <Select dataSource={algorithmList} name="algorithmId" placeholder="请选择算法" showClear={false}/>
                                </FormItem>
                                :
                                <FormItem disabled={true}>
                                    <Label>算法名称</Label>
                                    <Select dataSource={algorithmList} value={algorithmId} name="algorithmId" showClear={false}/>
                                </FormItem>
                            }
                            <FormItem>
                                <Label>模型名称</Label>
                                <Input name="modelName" placeholder="请输入模型名称" />
                            </FormItem>
                            <FormItem>
                                <Label>数据集</Label>
                                <Select dataSource={dataSetList} name="dataSetId" placeholder="请选择数据集" showClear={false}/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAdd.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">提交</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/model/mgmt/model/edit'}
                      trigger={ this.formEditTirgger }
                      async={true}
                      onSubmit={this.beforeEditSubmit}
                      onAfterSubmit={this.afterEditSubmit}
                >
                    <Modal visible={ editVisible } size="medium" onClose={ this.closeEdit.bind( this ) }>
                        <ModalHeader>
                            编辑模型
                        </ModalHeader>
                        <ModalBody>
                            <Input name="modelId" value={modelId} style={{display:'none'}} />
                            <Input name="algorithmId" value={algorithmId_echo} style={{display:'none'}} />
                            <FormItem disabled={true}>
                                <Label>算法名称</Label>
                                <Input value={algorithmName} />
                            </FormItem>
                            <FormItem>
                                <Label>模型名称</Label>
                                <Input name="newModelName" value={modelName} placeholder="请输入模型名称" />
                            </FormItem>
                            <FormItem>
                                <Label>数据集</Label>
                                <Select dataSource={dataSetList} value={dataSetId} name="newDataSetId" placeholder="请选择数据集" showClear={false}/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeEdit.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">提交</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/model/mgmt/model/upload'}
                      trigger={ this.formUploadTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={ this.beforeImportSubmit }
                      onAfterSubmit={this.afterImportSubmit}
                >
                    <Modal visible={ importVisible } size="medium" onClose={ this.closeImport.bind( this ) }>
                        <ModalHeader>
                            导入模型
                        </ModalHeader>
                        <ModalBody>
                            {algorithmName == '全部' ?
                                <FormItem>
                                    <Label>算法</Label>
                                    <Select dataSource={algorithmList} name="algorithmId" placeholder="请选择算法" showClear={false}/>
                                </FormItem>
                                :
                                <FormItem disabled={true}>
                                    <Label>算法名称</Label>
                                    <Select dataSource={algorithmList} value={algorithmId} name="algorithmId" showClear={false}/>
                                </FormItem>
                            }
                            <FormItem>
                                <Label>模型名称</Label>
                                <Input name="modelName" placeholder="请输入模型名称" />
                            </FormItem>
                            <div style={{maxHeight:'300px',overflow:'auto'}}>
                                <FormItem>
                                    <Label>文件上传</Label>
                                    <Upload multiple={true} placeholder="上传文件" name="files" trigger={ this.formFileTirgger }/>
                                </FormItem>
                            </div>
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
    }

}
ModelManage.UIPage = page;
export default ModelManage;