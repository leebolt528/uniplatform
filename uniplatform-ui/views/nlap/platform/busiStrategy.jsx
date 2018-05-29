import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,popup,Snackbar,Pagination,Text,CheckboxGroup,Checkbox,Transfer,
    Dialog,Upload,Dropdown} from 'epm-ui';
import { Footer,Com_Menu,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '业务策略管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css'
    ]
};

class BusinessStrategyManage extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            addNodeVisible:false,
            StrategiesList: [],
            siteData: [],
            dateData: [],
            taskData: [],
            unsucTaskData:[],
            busiHtml: [],
            isRemove: false,
            importVisible: false,
            strategyName: '',
            isEdit: false,
            nodeIdList: []
        }
        this.getFetchNodeList = this.getFetchNodeList.bind(this);
        this.beforeNodeSubmit = this.beforeNodeSubmit.bind(this);
        this.afterAddNodeSubmit = this.afterAddNodeSubmit.bind(this);
        this.formTirgger = this.formTirgger.bind(this);
        this.showBusiAddForm = this.showBusiAddForm.bind(this);
        this.handleSelect = this.handleSelect.bind( this );
        this.afterAddStrSubmit = this.afterAddStrSubmit.bind(this);
        this.formAddStrTirgger = this.formAddStrTirgger.bind(this);
        this.formUploadTirgger = this.formUploadTirgger.bind(this);
        this.afterImportSubmit = this.afterImportSubmit.bind(this);
        this.beforeImportSubmit = this.beforeImportSubmit.bind(this);
        this.afterUpdateStrSubmit = this.afterUpdateStrSubmit.bind(this);
        //this.handleNodeClick = this.handleNodeClick.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        //this.cancelRemove = this.cancelRemove.bind(this);
    }

    componentDidMount() {
        this.getFetchNodeList();
        this.getFetchBusinessStrategies();

    }

    //初始化节点列表
    getFetchNodeList(){
        fetch(Uniplatform.context.url + '/nlap/admin/business/mgmt/node/list', {
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
                let nodeList = [];
                data.businessNodes.nodeInfo.map(function (item) {
                    nodeList.push({
                        value : item.nodeId,
                        text : item.nodeName,
                        usingBusiness : item.usingBusiness,
                        status : item.status
                    });
                });
                this.setState({nodeList});
                this.showBusiAddForm();
            }).catch((err) => console.log(err.toString()));
    }

    //初始化业务策略列表
    getFetchBusinessStrategies(){
        let param = new FormData();
        let {size,index} = this.state;
        param.append('pageIndex', index);
        param.append('pageSize', size);
        param.append('keyword','');
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/business/mgmt/business/list', {
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
                if(data.businesses.numbers == 0){
                    noData = true;
                }
                let total = data.businesses.numbers;
                let StrategiesList = [];
                data.businesses.businessInfo.map(function (item) {
                    StrategiesList.push({
                        createTime : item.business.createTime,
                        inUsing : item.business.inUsing,
                        strategyId : item.business.id,
                        name : item.business.name,
                        taskNames : item.taskNames
                    });
                });
                this.setState({StrategiesList,total,noData});
            }).catch((err) => console.log(err.toString()));
    }



    //增加节点模态框
    showAddModal(){
        this.reset();
        this.getFetchStrategies();
        this.setState({ addNodeVisible : true });
    }
    closeAddStr(){
        this.resetAdd();
        this.setState({ addNodeVisible : false });
    }

    //获取功能策略列表
    getFetchStrategies(){
        fetch(Uniplatform.context.url + '/nlap/admin/business/mgmt/strategy/list', {
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
                let leftList = [];
                let rightList = [];
                data.listStrategies.map(function (item) {
                    leftList.push({
                        text: item.strategyName,
                        key: item.strategyId
                    });
                });
                this.setState({leftList,rightList});
            }).catch((err) => console.log(err.toString()));
    }

    exChangePagi(index,size){
        this.setState({index, size}, () => this.getFetchBusinessStrategies());
    }

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

    handleListChange(leftList,rightList) {
        let selectLabelId = '';
        for(let i=0;i<rightList.length;i++){
            if(i == rightList.length-1){
                selectLabelId += rightList[i].key;
            }else {
                selectLabelId += rightList[i].key + ',';
            }
        }
        this.setState({selectLabelId});
    }

    beforeNodeSubmit(data){
        let {selectLabelId} = this.state;
        let nodeName = data.nodeName.replace(/(^\s*)|(\s*$)/g, "");
        if(!nodeName){
            popup(<Snackbar message="节点名称不能为空"/>);
            return false;
        }else if (!selectLabelId){
            popup(<Snackbar message="节点内容不能为空"/>);
            return false;
        }else {
            let bodys = selectLabelId;
            let body = "strategyIds";
            data[body] = bodys;
            let nodename = 'nodeName';
            data[nodename] = nodeName;
            return data;
        }
    }

    afterAddNodeSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.reset();
                this.setState({ addNodeVisible : false },()=>this.resetAdd());
            }
        }
        this.setState(()=>this.getFetchNodeList());
    }
    afterAddStrSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.resetStr();
                this.getFetchBusinessStrategies();
                this.getFetchNodeList();
            }
        }
    }
    afterUpdateStrSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.getFetchBusinessStrategies();
                this.getFetchNodeList();
            }
        }
        this.setState({isEdit : false}, ()=> this.showBusiAddForm());
    }
    //重置表单
    formTirgger( trigger ) {
        this.resetAdd = trigger.reset;
    }
    formAddStrTirgger(trigger){
        this.resetStr = trigger.reset;
    }
    trigger( obj ) {
        this.reset = obj.reset;
    }

    /*handleNodeClick(){
        this.setState({isRemove : true});
        this.getFetchNodeList();
    }*/
    /*cancelRemove(){
        this.setState({isRemove : false});
        this.getFetchNodeList();
    }*/

    //删除节点
    deleteNode(nodeId){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleNodeApprove.bind(this,nodeId)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该节点?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleNodeApprove(nodeId,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/business/mgmt/node/del?nodesId='+nodeId , {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    popup(<Snackbar message={data.msg}/>);
                    if (data.status == 200){
                        this.getFetchNodeList();
                    }
                }
                after( true );
            }).catch((err) => {
            console.log(err.toString());
            after( true );
        });
    }


    //业务策略添加表单
    showBusiAddForm(){
        let {strategyId_echo,strategyName,isEdit,nodeList,isRemove,nodeIdList} = this.state;
        let busiHtml = [];
        if(isEdit){
            busiHtml.push(
                <Row className="dicManage_row" key="">
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col" style={{borderBottom:'1px solid red'}}>
                        <div className = "dicManage_title" >
                            <span className = "dicManage_span">业务策略编辑</span>
                        </div>
                    </Col>
                    <div className="dicManage_content" style={{border:'1px solid red'}}>
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'0 2%',padding:'2%',width:'95%'}}>
                            <Form
                                method="post"
                                action={contextUrl + '/nlap/admin/business/mgmt/business/edit'}
                                async={ true }
                                onSubmit={ this.handleUpdateSubmit }
                                onAfterSubmit={ this.afterUpdateStrSubmit }
                            >
                                <Input style={{ display:'none' }} name="businessId" value={strategyId_echo}/>
                                <FormItem>
                                    <Label style={{width:'70px',textAlign:'right'}}>业务名称：</Label>
                                    <Input name="newBusinessName" value={strategyName} placeholder="请输入业务名称" style={{display:'inline-block',width:'50%'}}/>
                                </FormItem>
                                <FormItem>
                                    <Row>
                                        <Col style={{padding:'0',width:'70px'}}>
                                            <Label style={{width:'70px'}}>业务功能：</Label>
                                        </Col>
                                        <Col size={{ normal: 22, small: 24, medium: 22, large: 22 }}>
                                            <CheckboxGroup name="newNodeIds" value={nodeIdList} type="inline">
                                                {
                                                    nodeList.map( (item,index) => {
                                                        return (
                                                            <Checkbox value={item.value} key={index} style={{width:'20%'}} >
                                                                {item.status ?
                                                                    <Dropdown position={'right'}>
                                                                        <Dropdown.Trigger action="hover">
                                                                            <span>{item.text}</span>
                                                                        </Dropdown.Trigger>
                                                                        <Dropdown.Content>
                                                                            <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                                {item.usingBusiness.map(function (busi,i) {
                                                                                    return (<p key={i} style={{margin:'0'}}>{busi.businssName}</p>);
                                                                                })}
                                                                            </div>
                                                                        </Dropdown.Content>
                                                                    </Dropdown>
                                                                    : <span>{item.text}</span>
                                                                }
                                                                &nbsp;&nbsp;
                                                                {item.status ? '' :
                                                                    <a onClick={this.deleteNode.bind(this,item.value)}>
                                                                        <i className="fa fa-times"></i>
                                                                    </a>
                                                                }
                                                            </Checkbox>
                                                        );
                                                    } )
                                                }
                                            </CheckboxGroup>
                                        </Col>
                                    </Row>
                                </FormItem>
                                <FormItem>
                                    <Label style={{width:'70px',textAlign:'right'}}>自定义：</Label>
                                    <a onClick={this.showAddModal.bind(this)}><i className="fa fa-plus-square-o fa-lg"></i></a>
                                </FormItem>
                                <Button type="primary" htmlType="submit" style={{left:'43%'}}>保存修改</Button>
                                <Button type="default" style={{left:'43%'}} onClick={this.cancelEdit.bind(this)}>返回</Button>
                            </Form>
                        </Col>
                    </div>
                </Row>
            );
        }else {
            busiHtml.push(
                <Row className="dicManage_row" key="">
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                        <div className = "dicManage_title" >
                            <span className = "dicManage_span">业务策略创建</span>
                        </div>
                    </Col>
                    <div className="dicManage_content">
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'0 2%',padding:'2%',width:'95%'}}>
                            <Form
                                method="post"
                                action={contextUrl + '/nlap/admin/business/mgmt/business/add'}
                                trigger={ this.formAddStrTirgger }
                                async={ true }
                                onSubmit={ this.handleSubmit }
                                onAfterSubmit={ this.afterAddStrSubmit }
                            >
                                <FormItem>
                                    <Label style={{width:'70px',textAlign:'right'}}>业务名称：</Label>
                                    <Input name="businessName" placeholder="请输入业务名称" style={{display:'inline-block',width:'50%'}}/>
                                </FormItem>
                                <FormItem>
                                    <Row>
                                        <Col style={{padding:'0',width:'70px'}}>
                                            <Label style={{width:'70px'}}>业务功能：</Label>
                                        </Col>
                                        <Col size={{ normal: 22, small: 24, medium: 22, large: 22 }}>
                                            <CheckboxGroup name="nodeIds" type="inline">
                                                {
                                                    nodeList.map( (item,index) => {
                                                        return(
                                                            <Checkbox value={item.value} key={index} style={{width:'20%'}} >
                                                                {item.status ?
                                                                    <Dropdown position={'right'}>
                                                                        <Dropdown.Trigger action="hover">
                                                                            <span>{item.text}</span>
                                                                        </Dropdown.Trigger>
                                                                        <Dropdown.Content>
                                                                            <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                                {item.usingBusiness.map(function (busi,i) {
                                                                                    return (<p key={i} style={{margin:'0'}}>{busi.businssName}</p>);
                                                                                })}
                                                                            </div>
                                                                        </Dropdown.Content>
                                                                    </Dropdown>
                                                                    : <span>{item.text}</span>
                                                                }
                                                                &nbsp;&nbsp;
                                                                {item.status ? '' :
                                                                    <a onClick={this.deleteNode.bind(this,item.value)}>
                                                                        <i className="fa fa-times"></i>
                                                                    </a>
                                                                }
                                                            </Checkbox>
                                                        )
                                                    } )
                                                }
                                            </CheckboxGroup>
                                        </Col>
                                    </Row>
                                </FormItem>
                                <FormItem>
                                    <Label style={{width:'70px',textAlign:'right'}}>自定义：</Label>
                                    <a onClick={this.showAddModal.bind(this)}><i className="fa fa-plus-square-o fa-lg"></i></a>
                                </FormItem>
                                <Button type="primary" htmlType="submit" style={{left:'43%'}}>确认添加</Button>
                            </Form>
                        </Col>
                    </div>
                </Row>
            );
        }
        this.setState({busiHtml});
    }

    //取消编辑
    cancelEdit(){
        this.setState({isEdit : false}, ()=> this.showBusiAddForm());
    }

    //添加业务策略提交信息
    handleSubmit(data){
        let nodeIds = data.nodeIds;
        let businessName = data.businessName.replace(/(^\s*)|(\s*$)/g, "");
        if(!businessName){
            popup(<Snackbar message="业务名称不能为空"/>);
            return false;
        }else if(nodeIds.length == 0){
            popup(<Snackbar message="业务功能不能为空"/>);
            return false;
        }else {
            let nodeId = '';
            for(let i=0; i<nodeIds.length; i++){
                if(i == nodeIds.length-1){
                    nodeId += nodeIds[i];
                }else {
                    nodeId += nodeIds[i] + ',';
                }
            }
            let bodys = 'nodeIds';
            data[bodys] = nodeId;
            let b = 'businessName';
            data[b] = businessName;
            return data;
        }
    }
    //编辑业务策略提交信息
    handleUpdateSubmit(data){
        let newNodeIds = data.newNodeIds;
        let newBusinessName = data.newBusinessName.replace(/(^\s*)|(\s*$)/g, "");
        if(!newBusinessName){
            popup(<Snackbar message="业务名称不能为空"/>);
            return false;
        }else if(newNodeIds.length == 0){
            popup(<Snackbar message="业务功能不能为空"/>);
            return false;
        }else {
            let nodeId = '';
            for(let i=0; i<newNodeIds.length; i++){
                if(i == newNodeIds.length-1){
                    nodeId += newNodeIds[i];
                }else {
                    nodeId += newNodeIds[i] + ',';
                }
            }
            let bodys = 'newNodeIds';
            data[bodys] = nodeId;
            let n = 'newBusinessName';
            data[n] = newBusinessName;
            return data;
        }
    }

    //表格多选
    handleSelect( data ) {
        this.setState( { selectedStr: data.map( ( key ) => key.strategyId ).join( ',' ),
            selectedInUsing: data.map( ( key ) => key.inUsing )} );
    }
    //批量删除
    batchDeleteStr(selectedStr,selectedInUsing){
        this.setState({ message : undefined });
        if(selectedStr.length == 0){
            popup(<Snackbar message="您未选择任何策略"/>);
        }else if((selectedInUsing.length ==1) &&(selectedInUsing != 0)){
            popup(<Snackbar message="该策略正在使用中！"/>);
        }else if(selectedInUsing.length > 1){
            for(let i=0; i<selectedInUsing.length; i++){
                if(selectedInUsing[i] != 0){
                    this.setState({ message : "所选策略中正在被使用的策略不能删除，其余已删除成功"});
                }
            }
            this.deleteStr(selectedStr);
        }else {
            this.deleteStr(selectedStr);
        }
    }
    //删除策略
    deleteStr(strId){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleApprove.bind(this,strId)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该策略?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleApprove(strId,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/business/mgmt/business/delete?businessesId='+strId , {
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
                        this.setState({isEdit : false, index : 1});
                        this.getFetchBusinessStrategies();
                        this.getFetchNodeList();
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

    //处理点击编辑业务策略操作
    handleEditClick(strId,strName){
        $(document).scrollTop(0);
        this.getFetchSingleStr(strId);
        this.setState({strategyId_echo : strId,strategyName : strName, isEdit : true}, () => this.showBusiAddForm());
    }
    //获取单个策略信息（编辑策略回显信息）
    getFetchSingleStr(strId){
        fetch(Uniplatform.context.url + '/nlap/admin/business/mgmt/single/business/info?businessId='+strId , {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                let nodeName = '';
                let nodeIdList = [];
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }else {
                    data.nodeInfoList.map(function (item) {
                        nodeName = item.nodeName;
                        nodeIdList.push(item.nodeId);
                    });
                }
                this.setState({nodeIdList});
                this.showBusiAddForm();
            }).catch((err) => console.log(err.toString()));
    }

    //导入策略
    importStrModal(){
        this.setState({ importVisible : true });
    }
    closeImportStr(){
        this.resetUpload();
        this.setState({ importVisible : false });
    }
    beforeImportSubmit(data){
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
                this.getFetchBusinessStrategies();
            }
        }
    }

    //导出策略
    exportStr(selectedStr){
        if(selectedStr.length == 0){
            popup(<Snackbar message="您未选择任何策略"/>);
        }else {
            window.open(`${Uniplatform.context.url}/nlap/admin/business/mgmt/businesses/export?businessesId=${selectedStr}`,'_self');
        }
    }


    render() {
        let {addNodeVisible,StrategiesList,index,total,size,leftList,rightList,busiHtml,selectedStr,importVisible,
            selectedInUsing,noData} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'980px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <NLPMenu url={'/nlap/platform/busiStrategy'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0',marginTop:'20px'}}>
                        {busiHtml}
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">业务策略管理</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <ButtonGroup style={{float:'right',margin:'15px'}}>
                                    <Button onClick={this.importStrModal.bind(this)}><i className="fa fa-upload"></i> 导入策略</Button>
                                    {noData ?
                                        <Button disabled><i className="fa fa-download"></i> 导出策略</Button>
                                        :
                                        <Button onClick={this.exportStr.bind(this,selectedStr)}><i className="fa fa-download"></i> 导出策略</Button>
                                    }
                                    {noData ?
                                        <Button disabled><i className="fa fa-trash"></i> 批量删除</Button>
                                        :
                                        <Button onClick={this.batchDeleteStr.bind(this,selectedStr,selectedInUsing)}><i className="fa fa-trash"></i> 批量删除</Button>
                                    }

                                </ButtonGroup>

                                {noData ?
                                    <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                        暂无数据
                                    </div>
                                    :
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                        <Table  striped={true} multiLine={true} checkable headBolder style={{color:'#54698d'}}
                                                onCheck={ this.handleSelect } dataSource={StrategiesList}>
                                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                { ( value, index ) => { return Number(`${Number(this.state.index-1)*Number(this.state.size)+Number(index)}`) + 1; } }
                                            </Column>
                                            <Column dataIndex="name" title="策略名称" scaleWidth='20%' textAlign="center"/>
                                            <Column dataIndex="user" title="创建者" scaleWidth='20%' textAlign="center"/>
                                            <Column title="状态" scaleWidth='15%' textAlign="center">
                                                {(value)=>{
                                                    let status = [];
                                                    switch (value.inUsing){
                                                        case 0:
                                                            status.push('未使用');
                                                            break;
                                                        case 1:
                                                            status.push(
                                                                <Dropdown position={'right'} key="">
                                                                    <Dropdown.Trigger action="hover">
                                                                        <span>使用中</span>
                                                                    </Dropdown.Trigger>
                                                                    <Dropdown.Content>
                                                                        <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                            {value.taskNames.map(function (task,i) {
                                                                                return (<p key={i} style={{margin:'0'}}>{task}</p>);
                                                                            })}
                                                                        </div>
                                                                    </Dropdown.Content>
                                                                </Dropdown>
                                                            );
                                                            break;
                                                        default:
                                                            status = '';
                                                            break;
                                                    }
                                                    return status;
                                                }}
                                            </Column>
                                            <Column title="创建时间" scaleWidth='20%' textAlign="center">
                                                {(value)=>{
                                                    return this.switchTime(value.createTime);
                                                }}
                                            </Column>
                                            <Column title="操作" scaleWidth='20%' textAlign="center" >
                                                {( value )=>{
                                                    return(
                                                        <div>
                                                            {value.inUsing ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    <i className="edit"></i> 编辑
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.handleEditClick.bind(this,value.strategyId,value.name)}>
                                                                    <i className="edit"></i> 编辑
                                                                </Button>
                                                            }
                                                            {value.inUsing ?
                                                                <Button type="default" size="tiny" disabled>
                                                                    <i className="trash-o"></i> 删除
                                                                </Button>
                                                                :
                                                                <Button type="default" size="tiny" onClick={this.deleteStr.bind(this,value.strategyId)}>
                                                                    <i className="trash-o"></i> 删除
                                                                </Button>
                                                            }
                                                            <Button type="default" size="tiny" onClick={this.exportStr.bind(this,value.strategyId)}>
                                                                <i className="trash-o"></i> 导出
                                                            </Button>
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

                <Form type="inline"
                      method="post"
                      action={contextUrl + '/nlap/admin/business/mgmt/node/add'}
                      trigger={ this.formTirgger }
                      async={true}
                      onSubmit={ this.beforeNodeSubmit }
                      onAfterSubmit={this.afterAddNodeSubmit}
                >
                    <Modal visible={ addNodeVisible } size="medium" onClose={ this.closeAddStr.bind( this ) }>
                        <ModalHeader>
                            创建节点
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>节点名称</Label>
                                <Input name="nodeName" placeholder="请输入节点名称" />
                            </FormItem>
                            <FormItem>
                                <Transfer
                                    style={{left:'67px'}}
                                    leftList={ leftList }
                                    rightList={ rightList }
                                    render={ item => `${ item.text }` }
                                    titles={ ['功能策略名称', '节点内容'] }
                                    onChange={ this.handleListChange.bind(this) }
                                    trigger={ this.trigger.bind( this ) }
                                />
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddStr.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Form type="inline"
                      method="post"
                      action={contextUrl + '/nlap/admin/business/mgmt/businesses/import'}
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={ this.beforeImportSubmit }
                      onAfterSubmit={this.afterImportSubmit}
                >
                    <Modal visible={ importVisible } size="medium" onClose={ this.closeImportStr.bind( this ) }>
                        <ModalHeader>
                            导入策略
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>文件上传</Label>
                                <Upload placeholder="上传文件" name="files" multiple={true} trigger={ this.formUploadTirgger }/>
                            </FormItem>
                            <Divider/>
                            <span style={{ float:"left",textAlign:"left",marginLeft:"40px"}}> 1、 系统支持.xml格式的文件进行上传。<br/>
                                    2、.xml文件的<a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=business.xml">示例文件</a>
                            </span>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeImportStr.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Footer />
            </Page>
        );
    }

}
BusinessStrategyManage.UIPage = page;
export default BusinessStrategyManage;