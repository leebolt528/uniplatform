import React, {Component} from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Text,Pagination,context,Modal,ModalHeader,
    ModalBody,ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,Checkbox,Dialog,Option,Select,Upload,Tabs,Tab,
    ButtonGroup,Container,Textarea,PagiTable} from 'epm-ui';
import {SingleBanner,Footer,COMM_HeadBanner,UsouMenu,NavigationBar} from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform/usou/monitor/';

const page = {
    title: 'Usou',
    css: [
        'css/clusters.min.css',
        'css/singleBanner.min.css',
        'css/leftnav.min.css'
    ]
};

class monitorManage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            monitor: false,
            size:5,
            index:1,
            total:0,
            dataSource:[],
            indexPartition:1,
            selectName:[],
            resultSource:[],
            resultListSource:[],
            indexListResult:1,
            totalListResult:0,
            visibleResult:false,
            totalResult:0,
            indexResult:1,
            defaultNodes:0,
            alterMonitor:{},
            alterOrAdd:"添加"
        };

        this.addMonitor = this.addMonitor.bind(this);
        this.formTirggerStatus = this.formTirggerStatus.bind(this);
        this.formGetterStatus = this.formGetterStatus.bind(this);
        this.formGetterStatusCheck=this.formGetterStatusCheck.bind(this);
        this.formGetterCpuCheck=this.formGetterCpuCheck.bind(this);
        this.formGetterCpu=this.formGetterCpu.bind(this);
        this.formGetterDisk=this.formGetterDisk.bind(this);
        this.formGetterDiskCheck=this.formGetterDiskCheck.bind(this);
        this.formGetterMemoryCheck=this.formGetterMemoryCheck.bind(this);
        this.formGetterMemory=this.formGetterMemory.bind(this);
        this.formGetterContact=this.formGetterContact.bind(this);
        this.formGetterStyle=this.formGetterStyle.bind(this);
        this.formGetterNodeCheck=this.formGetterNodeCheck.bind(this);
        this.formGetterNode=this.formGetterNode.bind(this);
        this.formGetterName=this.formGetterName.bind(this);
        this.formGetterHour=this.formGetterHour.bind(this);
        this.formGetterMin=this.formGetterMin.bind(this);
        this.confirmBatchDelete=this.confirmBatchDelete.bind(this);
        this.handleSelect=this.handleSelect.bind(this);
        this.batchDelete=this.batchDelete.bind(this);

    };

    addMonitor() {
        this.setState({alterOrAdd:"添加"});
        this.setState({monitor: true});
        this.defaultNodesValue();
    }

    handleClose() {
        this.setState({monitor: false})
    }

    handleResultClose() {
        this.setState({visibleResult: false})
    }

    componentDidMount(){
        this.getMonitorList(this.state.index,this.state.size);
        this.getMonitorResultList(this.state.indexListResult,this.state.size);
    }

    exchangeListPageResult(index,size){
        this.getMonitorResultList(index,size);
        this.setState({indexListResult:index});
    }

    getMonitorResultList(index,size){
        let group="group=USOU";
        let pageNum="pageNum="+index;
        let pageSize="pageSize="+size;
        fetch("/uniplatform/common/alert/list/"+"?"+group+"&"+pageNum+"&"+pageSize)
            .then((response)=>response.json())
            .then((responseData)=>{
                let results = this.parseResult(responseData.data.content);
                this.setState({resultListSource:results});
                this.setState({totalListResult:responseData.data.totalElements});
            })
            .catch((error)=>console.log(error));
    }

    getMonitorResult(id){
        let group="group="+4;
        fetch("/uniplatform/common/alert/list/"+id+"?"+group,{method:'get'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let results = this.parseResult(responseData.data.content);
                this.setState({resultSource:results});
                this.setState({totalResult:results.length});
            })
            .catch((err)=>console.log(err));
        this.setState({visibleResult:true});
    }

    parseResult(results){
        for(let result of results){
            let receiver = JSON.parse(result.receiver);
            result.receiver=receiver.email[0].value;

            let date = new Date(result.createdTime);
            result.createdTime=date.toLocaleString();
        }
        return results;
    }

    defaultNodesValue(){
        fetch("/uniplatform/usou/cluster/"+this.props.page.id+"/health",{method:'get', credentials: 'same-origin'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let value =responseData.data.number_of_nodes>3
                    ?Math.floor(responseData.data.number_of_nodes/3):responseData.data.number_of_nodes;
                this.setState({defaultNodes:value});
            })
            .catch((err)=>console.log(err));
    }

    getMonitorList(index ,size){
        let pageNum="pageNum="+index;
        let pageSize="pageSize="+size;
        let cluster="cluster="+this.props.page.id;

        fetch(contextUrl+"/taskInfo/list?"+pageNum+"&"+pageSize+"&"+cluster,{method:'get'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let dataSource = this.parseDataSource(responseData.data.content);
                this.setState({dataSource:dataSource});
                this.setState({total:responseData.data.totalElements});
                })
            .catch((err)=>console.log(err));
    }

    exchangePage(index,size){
        this.setState({index:index});
        this.setState({size:size});
        this.getMonitorList(index,size);
        console.log(index)
    }

    exchangePageResult(index,size){
        this.setState({indexResult:index});
    }

    parseDataSource(data){
        let array = new Array();
        for(let receive of data){
            let dataSource = {};
            dataSource.name=receive.name;
            let threshold = this.parseThreshold(receive.threshold);
            dataSource.threshold=threshold;
            dataSource.mode="邮件";
            let email = this.parseEmail(receive.receivers);
            dataSource.contact=email;
            dataSource.id=receive.id;
            array.push(dataSource);
        }
        return array;
    }

    parseEmail(email){
        let emailJson = JSON.parse(email);
        let values=emailJson.email;
        let contect="";
        for (let email of values){
            contect+=email.value+";";
        }
        return contect;
    }

    parseThreshold(threshold){
        let thresholdJson = JSON.parse(threshold);
        let result="";
        for(let data of thresholdJson){
            let name= data.name;
            let operator=data.operator;
            let value=data.value+";";
            result+=name;
            result+=operator;
            result+=value;
        }
        return result;
    }

    handleClick() {
        let cpuCheck = this.getValueCpuCheck();
        let diskCheck = this.getValueDiskCheck();
        let memoryCheck = this.getValueMemoryCheck();
        let statusCheck = this.getValueStatusCheck();
        let nodeCheck = this.getValueNodeCheck();

        let taskInfo = new Object();
        let thresholds = new Array();
        if(this.NullOfEmpty(cpuCheck)){
            let cpu = this.getValueCpu();
            let threshold = new Object();
            threshold.name="cpu";
            threshold.operator=">=";
            threshold.value=cpu;
            thresholds.push(threshold);
        }
        if (this.NullOfEmpty(diskCheck)){
            let disk = this.getValueDisk();
            let threshold = new Object();
            threshold.name="fs";
            threshold.operator=">=";
            threshold.value=disk;
            thresholds.push(threshold);
        }
        if(this.NullOfEmpty(memoryCheck)){
            let memory = this.getValueMemory();
            let threshold = new Object();
            threshold.name="mem";
            threshold.operator=">=";
            threshold.value=memory;
            thresholds.push(threshold);
        }
        if(this.NullOfEmpty(statusCheck)){
            let status = this.getValueStatus();
            let threshold = new Object();
            threshold.name="health";
            threshold.operator=">=";
            threshold.value=status;
            thresholds.push(threshold);
        }

        let node = this.getValueNode();
        node=this.NullOfEmpty(node)?node:"1";
        let threshold = new Object();
        threshold.name="num";
        threshold.operator=">=";
        threshold.value=node;
        thresholds.push(threshold);
        let thresholdsStr = JSON.stringify(thresholds);
        let name = this.getValueName();

        taskInfo.name=name;
        taskInfo.threshold=thresholdsStr;

        let hour = this.getValueHour();
        let min = this.getValueMin();
        let frequency = new Object();

        frequency.hour=hour;
        frequency.min=min;

        taskInfo.frequency=JSON.stringify(frequency);
        let receivers = this.parseReceivers();

        taskInfo.receivers=receivers;

        let url = contextUrl+"/taskInfo/save";

        let data = new FormData();
        data.append("name",name);
        data.append("threshold",thresholdsStr);
        data.append("frequency",JSON.stringify(frequency));
        data.append("receivers",receivers);
        data.append("beanId",this.props.page.id);

        if(this.state.alterOrAdd=="修改"){
            url = contextUrl+"/taskInfo/update";
            data.append("id",this.state.alterMonitor.id);
            data.append("taskId",this.state.alterMonitor.taskId);

            fetch(url,{method:'post',body:data}).then((response)=>response.json())
                .then((responseData)=>{
                    if (responseData.code==1||responseData.status==200){
                        this.showDialogSuccess(this.state.alterOrAdd+"预警",this.state.alterOrAdd+"成功") ;
                        this.getMonitorList(this.state.index,this.state.size);
                    } else {
                        this.showDialogFail(this.state.alterOrAdd+"预警",this.state.alterOrAdd+"失败");
                    }
                }).catch((error)=>console.log(error));

        }else if(this.state.alterOrAdd=="添加"){
            fetch(url,{method:'post',body:data}).then((response)=>response.json())
                .then((responseData)=>{
                    if (responseData.code==1||responseData.status==200){
                        this.showDialogSuccess(this.state.alterOrAdd+"预警",this.state.alterOrAdd+"成功") ;
                        this.getMonitorList(this.state.index,this.state.size);
                    } else {
                        this.showDialogFail(this.state.alterOrAdd+"预警",this.state.alterOrAdd+"失败");
                    }
                }).catch((error)=>console.log(error));
        }

        this.setState({monitor: false})
    }

    showDialogSuccess(title,message) {
        popup(<Dialog
            title={`${title}`}
            message={`${message}`}
        />);
    }

    showDialogFail(title,message) {
        popup(<Dialog
            title={`${title}`}
            message={`${message}`}
            icon="danger"
        />);
    }

    parseReceivers(){
        let contact = this.getValueContact();
        let emails = contact.split(";");
        let style = this.getValueStyle();
        let contectEmail = new Array();
        for (let email of emails){
            if (this.nullOrEmpty(email)){
                let userValue = {};
                userValue.user="futao";
                userValue.value=email;
                contectEmail.push(userValue);
            }
        }
        let receiver = {};
        receiver.email=contectEmail;

        return JSON.stringify(receiver);
    }

    nullOrEmpty(data){
        if (data==null||data==""||data==undefined){
            return false;
        }
        return true;
    }

    alterMonitor(id){
        this.setState({alterOrAdd:"修改"});
        let url=contextUrl+"/taskInfo/get/"+id;
        fetch(url,{method:'get'})
            .then((response)=>response.json())
            .then((responseData)=>{
                console.log(responseData.data);
                let data=this.parseAlter(responseData.data);
                console.log(data);
                this.setState({alterMonitor:data});
                this.setState({monitor:true});
            })
            .catch((err)=>console.log(err));
    }

    parseAlter(result){
        let alter={};

        let freq=result.frequency.split(" ");
        alter.min=freq[1];
        alter.hour=freq[2];
        alter.name=result.name;
        let thresholds=JSON.parse(result.threshold);
        for (let threshold of thresholds){
            switch(threshold.name){
                case "cpu":
                    alter.cpu=threshold.value;
                    break;
                case "fs":
                    alter.fs=threshold.value;
                    break;
                case "mem":
                    alter.mem=threshold.value;
                    break;
                case "health":
                    alter.health=new String(threshold.value);
                    break;
                case "num":
                    alter.num=threshold.value;
                    break;
                default:
                    console.log("do't select things")
            }
        }
        let receivers=JSON.parse(result.receivers);
        let emails="";
        for (let index in receivers.email){
            let email;
            if (index==(receivers.email.length-1)){
                email=receivers.email[index].value;
            }else {
                email=receivers.email[index].value+";";
            }
            emails+=email;
        }
        alter.email=emails;
        alter.receivers="1";
        alter.id=result.id;
        alter.taskId=result.taskId;
        return alter;

    }

    confirmBatchDelete(){
        if (this.state.selectName==null||this.state.selectName.length==0){
            this.noSelect();
        }else {
            const approveBtn = {
                text: "确认",
                type: "primary",
                onClick: this.batchDelete
            };
            const cancelBtn = { text: "取消" }
            popup(<Dialog
                title="批量删除"
                message="确认批量删除"
                type="confirm"
                showCancelBtn
                cancelBtn={ cancelBtn }
                showApproveBtn
                approveBtn = { approveBtn }
            />);
        }

    }

    batchDelete( after ){
        let select = this.state.selectName;
        for(let name in select){
            let url=contextUrl+"/taskInfo/delete/"+select[name];
            fetch(url,{method:'get'}).then((response)=>response.json())
                .then((responseData)=>{
                    if (name==select.length-1) this.getMonitorList(this.state.index,this.state.size);

                }).catch((error)=>console.log(error));
        }
        after( true );
    }

    handleSelect( data ) {
        let names = [];
        for(let page of data){
            names.push(page.id);
        }
        this.setState( { selectName: names});
    }

    noSelect(){
        popup(<Dialog
            title="是否选择"
            message="您没有选择，请重新选择"
            icon="warn"
        />);
    }

    confirmDelete(name){
        let url=contextUrl+"/taskInfo/delete/"+name;
        fetch(url,{method:'get'}).then((response)=>response.json())
            .then((responseData)=>{
            if(responseData.code==1||responseData.status==200){
                this.showDialogSuccess("删除预警","删除成功");
                this.state.total%this.state.size==1?this.setState({index:this.state.index-1}):this.state.index;
                this.getMonitorList(this.state.index,this.state.size);
            }else {
                this.showDialogFail("删除预警","删除失败");
            }
        }).catch((error)=>console.log(error));

    }


    NullOfEmpty(value){
        if (value==undefined||value==null||value==""){
            return false;
        }
        return true;
    }

    formTirggerStatus(trigger) {
        this.resetStatus = trigger.reset;
    }

    formGetterStatus(getter) {
        this.getValueStatus = getter.value;
    }

    formGetterStatusCheck(getter) {
        this.getValueStatusCheck = getter.value;
    }

    formGetterCpuCheck(getter) {
        this.getValueCpuCheck = getter.value;
    }

    formGetterCpu(getter) {
        this.getValueCpu = getter.value;
    }

    formGetterDisk(getter) {
        this.getValueDisk = getter.value;
    }

    formGetterDiskCheck(getter) {
        this.getValueDiskCheck = getter.value;
    }

    formGetterMemoryCheck(getter) {
        this.getValueMemoryCheck = getter.value;
    }

    formGetterMemory(getter) {
        this.getValueMemory = getter.value;
    }

    formGetterContact(getter) {
        this.getValueContact = getter.value;
    }

    formGetterName(getter) {
        this.getValueName = getter.value;
    }

    formGetterStyle(getter) {
        this.getValueStyle = getter.value;
    }

    formGetterNodeCheck(getter) {
        this.getValueNodeCheck = getter.value;
    }

    formGetterNode(getter) {
        this.getValueNode = getter.value;
    }

    formGetterHour(getter) {
        this.getValueHour = getter.value;
    }

    formGetterMin(getter) {
        this.getValueMin = getter.value;
    }

    render() {
        let {monitor,dataSource,index,total,size,resultSource,resultListSource,visibleResult
            ,totalResult,indexResult,defaultNodes,alterMonitor,alterOrAdd,indexListResult,totalListResult} = this.state;

        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Divider/>
                <Row>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/monitorManage_Result'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }} style={{minHeight: '600px'}}>
                        <Row>
                            <NavigationBar code={'monitorManage_Result'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        </Row>
                        <Table dataSource={ resultListSource } striped={ true } multiLine={ true } >
                            <Column title="预警名称" dataIndex="infoName" scaleWidth = '20%' textAlign="center"/>
                            <Column title="信息" dataIndex="message" scaleWidth = '20%' textAlign="center"/>
                            <Column title="生成时间" dataIndex="createdTime"  scaleWidth = '20%' textAlign="center"/>
                            <Column title="接收者" dataIndex="receiver"  scaleWidth = '20%' textAlign="center"/>
                        </Table>
                        <Divider />
                        <Pagination name="pages"  index={ indexListResult }  total={totalListResult>1000?1000:totalListResult } size={ size } align='right'
                                    onChange={this.exchangeListPageResult.bind(this)}/>
                    </Col>
                </Row>

                <Modal visible={ monitor } onClose={ this.handleClose.bind(this) }>
                    <ModalHeader>
                        {alterOrAdd+"预警"}
                    </ModalHeader>
                    <ModalBody height={ 420 }>

                        <div style={{border:'1px hotpink solid',marginTop:'20px',width: '560px',marginRight:'auto',
                            marginLeft:'auto'}}>
                            <div style={{margin: '12px 0px 12px 12px'}}>
                                <p>预警配置</p>
                            </div>
                            <div style={{'borderTop': '1px solid hotpink'}}>
                                <Row style={{marginTop: '12px',marginBottom: '12px'}}>
                                    <Col size={6}>
                                        <p> 预警名称 </p>
                                    </Col>

                                    <Col size={14}>
                                        <Input value={alterMonitor.name} name="user" placeholder="设置预警名称"
                                               getter={ this.formGetterName } required={true}/>
                                    </Col>
                                </Row>

                                <Row style={{marginTop: '12px',marginBottom: '12px'}}>
                                    <Col size={6}>
                                        <p> 间隔时间 </p>
                                    </Col>

                                    <Col size={4} style={{paddingRight: '0px'}}>
                                        <Input value={alterMonitor.hour} type="number" name="user" placeholder="相隔小时数"
                                               getter={ this.formGetterHour } required={true}/>
                                    </Col>
                                    <Col size={3}>
                                        <p> 小时 </p>
                                    </Col>

                                    <Col size={4} style={{paddingLeft: '0px'}}>
                                        <Input value={alterMonitor.min} type="number" name="user" placeholder="相隔分钟数"
                                               getter={ this.formGetterMin } required={true}/>
                                    </Col>

                                    <Col size={3}>
                                        <p> 分钟 </p>
                                    </Col>
                                </Row>
                            </div>
                        </div>

                        <div style={{border:'1px hotpink solid',marginTop:'20px',width: '560px',marginRight:'auto',
                            marginLeft:'auto'}}>
                            <div style={{margin: '12px 0px 12px 12px'}}>
                                <p>预警规则</p>
                            </div>
                            <div style={{'borderTop': '1px solid hotpink'}}>
                                <Row style={{marginTop: '12px'}}>
                                    <Col size={6}>
                                        <Checkbox checked={this.nullOrEmpty(alterMonitor.health)}
                                                  value="集群状态" getter={ this.formGetterStatusCheck }
                                                  style={{marginTop:'8px'}}>
                                            集群状态
                                        </Checkbox>
                                    </Col>
                                    <Col size={2}>
                                        <p> >= </p>
                                    </Col>

                                    <Col size={12}>
                                        <Select  value={alterMonitor.health} placeholder="Please select" name="select"
                                                trigger={ this.formTirggerStatus } getter={ this.formGetterStatus }>
                                            <Option value="1">绿</Option>
                                            <Option value="2">黄</Option>
                                            <Option value="3">红</Option>
                                        </Select>
                                    </Col>
                                </Row>

                                <Row style={{marginTop: '12px'}}>
                                    <Col size={6}>
                                        <Checkbox checked={this.nullOrEmpty(alterMonitor.cpu)} value="CPU使用率"
                                                  getter={ this.formGetterCpuCheck }
                                                  style={{marginTop:'8px'}}>
                                            CPU使用率
                                        </Checkbox>
                                    </Col>
                                    <Col size={2}>
                                        <p> >= </p>
                                    </Col>

                                    <Col size={10} style={{paddingRight: '0px'}}>
                                        <Input value={alterMonitor.cpu} name="user" placeholder="90"
                                               getter={ this.formGetterCpu }/>
                                    </Col>

                                    <Col size={1}>
                                        <p> % </p>
                                    </Col>
                                </Row>

                                <Row style={{marginTop: '12px'}}>
                                    <Col size={6}>
                                        <Checkbox checked={this.nullOrEmpty(alterMonitor.fs)}
                                                  value="硬盘使用率" getter={ this.formGetterDiskCheck }
                                                  style={{marginTop:'8px'}}>
                                            硬盘使用率
                                        </Checkbox>
                                    </Col>
                                    <Col size={2}>
                                        <p> >= </p>
                                    </Col>

                                    <Col size={10} style={{paddingRight: '0px'}}>
                                        <Input value={alterMonitor.fs} name="user" placeholder="90"
                                               getter={ this.formGetterDisk }/>
                                    </Col>

                                    <Col size={1}>
                                        <p> % </p>
                                    </Col>
                                </Row>

                                <Row style={{marginTop: '12px',marginBottom: '12px'}}>
                                    <Col size={6} >
                                        <Checkbox checked={this.nullOrEmpty(alterMonitor.mem)}
                                                  value="内存使用率" getter={ this.formGetterMemoryCheck }
                                                  style={{marginTop:'8px'}}>
                                            内存使用率
                                        </Checkbox>
                                    </Col>
                                    <Col size={2}>
                                        <p> >= </p>
                                    </Col>

                                    <Col size={10} style={{paddingRight: '0px'}}>
                                        <Input value={alterMonitor.mem} name="user" placeholder="90"
                                               getter={ this.formGetterMemory }/>
                                    </Col>

                                    <Col size={1}>
                                        <p> % </p>
                                    </Col>
                                </Row>
                                <Row style={{marginTop: '12px',marginBottom: '12px'}}>
                                    <Col size={6} >
                                        <Checkbox value="最少节点数" checked={true} readonly={true}
                                                  getter={ this.formGetterNodeCheck }
                                                  style={{marginTop:'8px'}}>
                                            最少节点数
                                        </Checkbox>
                                    </Col>
                                    <Col size={2}>
                                        <p> >= </p>
                                    </Col>

                                    <Col size={12}>
                                        <Input value={defaultNodes==0?alterMonitor.num:defaultNodes} type="number" name="user" placeholder="节点数"
                                               getter={ this.formGetterNode } required={true}/>
                                    </Col>
                                </Row>
                            </div>
                        </div>

                        <div style={{border:'1px hotpink solid', marginTop: '40px',marginBottom: '50px'
                            ,width: '560px',marginRight:'auto', marginLeft:'auto'}}>
                            <div style={{margin: '12px 0px 12px 12px'}}>
                                <p>预警方式</p>
                            </div>
                            <div style={{'borderTop': '1px solid hotpink'}}>
                                <Row style={{marginTop: '12px'}}>
                                    <Col size={6}>
                                        <p> 预警方式</p>
                                    </Col>
                                    <Col size={14}>
                                        <Select value={alterMonitor.receivers} placeholder="邮件" name="select"
                                                getter={ this.formGetterStyle }>
                                            <Option value="1">邮件</Option>
                                        </Select>
                                    </Col>
                                </Row>

                                <Row style={{marginTop: '12px',marginBottom: '12px'}}>
                                    <Col size={6}>
                                        <p> 联系方式 </p>
                                    </Col>

                                    <Col size={14}>
                                        <Input value={alterMonitor.email} name="user" placeholder="请输入邮箱地址,多个地址;隔开"
                                               getter={ this.formGetterContact } required={true}
                                               pattern= { /^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/ }/>
                                    </Col>
                                </Row>
                            </div>
                        </div>
                        <div style={{float:'right'}}>
                        <Button onClick={ this.handleClose.bind( this ) }>取消</Button>
                        <Button onClick={ this.handleClick.bind( this )} type="primary" htmlType="submit" >确定</Button>
                        </div>
                    </ModalBody>
                </Modal>

                <Modal visible={ this.state.visibleResult } onClose={ this.handleResultClose.bind( this ) } >
                    <ModalHeader>
                        预警结果
                    </ModalHeader>
                    <ModalBody height={ 300 }>
                        <Table dataSource={ resultSource } striped={ true } multiLine={ true } >
                            <Column title="预警名称" dataIndex="infoName" scaleWidth = '20%' textAlign="center"/>
                            <Column title="信息" dataIndex="message" scaleWidth = '20%' textAlign="center"/>
                            <Column title="生成时间" dataIndex="createdTime"  scaleWidth = '20%' textAlign="center"/>
                            <Column title="接收者" dataIndex="receiver"  scaleWidth = '20%' textAlign="center"/>
                        </Table>
                        <Divider />
                        <Pagination name="pages"  index={ indexResult }  total={totalResult>1000?1000:totalResult } size={ size } align='right'
                                    onChange={this.exchangePageResult.bind(this)}/>
                    </ModalBody>
                </Modal>
                <Footer/>

            </Page>)
    }
}
monitorManage.UIPage = page;
export default monitorManage;