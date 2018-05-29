import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,DateTime,Dialog,Dropdown,Progress} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '规则管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/rules.min.css'
    ]
};

class RuleManage extends Component{
     constructor(props){
        super(props);

        this.state = {
            isNodeDetail:true,
            templatesData:[],
            index:1,
            size:10,
            size0:15,
            total:0,
            index0:1,
            total0:0,
            ruleType:[],
            ruleTypeId:0,
            ruleTypePart:[],
            nodeData:[],
            templateId:0,
            tem_index:0,
            nodeId:0,
            nodeRulesData:[],
            nodeRulesDataAll:[],
            size1:10,
            index1:1,
            total1:0,
            editRuleVisible:false,
            ruleId:0,
            addRuleVisible:false,
            uploadRuleVisible:false,
            addTemVisible:false,
            addNodeVisible:false,
            selectData:[],
            selectDataId:[],
            selectAllDataId:[],
            pageStart:0,
            pageBool:true,
            colorIndex:true,
            ruleName:"",
            addTemId:0,
            addRuleId:0,
            addOpera:"",
            selectBool:false,
            special:true,
            specialFirst:true,
            resultTemBool:true,
            firstIn:true,
            progressBool:false,
            searchTableBool:false,
            selectReplace:true
        }
        this.changeNode=this.changeNode.bind(this);
        this.changeTemplate=this.changeTemplate.bind(this);
        this.filterTemplates=this.filterTemplates.bind(this);
        this.exchangePage=this.exchangePage.bind(this);
        this.exchangePage0=this.exchangePage0.bind(this);
        this.exchangePage1=this.exchangePage1.bind(this);
        this.nodeRules=this.nodeRules.bind(this);
        this.editRuleModal=this.editRuleModal.bind(this);
        this.beforeEditSubmit=this.beforeEditSubmit.bind(this);
        this.afterEditSubmit = this.afterEditSubmit.bind(this);
        this.delRulePrompt=this.delRulePrompt.bind(this);
        this.getOpera=this.getOpera.bind(this);
        this.formGetterAdd=this.formGetterAdd.bind(this);
        this.formGetterEdit=this.formGetterEdit.bind(this);

        this.formTirggerTem=this.formTirggerTem.bind(this);
        this.formTirggerNode=this.formTirggerNode.bind(this);
        this.formTirggerEdit=this.formTirggerEdit.bind(this);
        this.formTirggerAdd=this.formTirggerAdd.bind(this);
        this.formTirggerUpload=this.formTirggerUpload.bind(this);
    }
    componentDidMount() {
       this.fetchDicTypes();
    }

    //获取词库类型
    fetchDicTypes(){
         fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/initRuleType', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            let ruleTypeId=data.ruleType[0].id;
            let {ruleType}=data;
            let ruleTypePart=data.ruleType.slice(1,data.ruleType.length);
            this.setState({ ruleType,ruleTypeId,ruleTypePart},()=>this.fetchTemplateList());
            //this.fetchTemplateList();
        }).catch((err) => console.log(err.toString()));
    }
    //获取模板
    fetchTemplateList(){
        let param = new FormData();
        param.append('pageSize',this.state.size0);
        param.append('pageIndex',this.state.index);
        param.append('dicTypeId',this.state.ruleTypeId);

        fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/filterTemplates', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let templatesData = data.templates;
            let total=data.totalNumber;
            let firstIn=false;
            this.setState({ templatesData,total,firstIn });
        }).catch((err) => console.log(err.toString()));
            
    }
    //获取节点
    fetchNodeList(){
        let {nodeId,addRuleId,selectBool,colorIndex,special,specialFirst}=this.state;
        let param = new FormData();
        param.append('templateId',this.state.templateId);
        param.append('pageSize',this.state.size);
        param.append('pageIndex',this.state.index0);

        fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/listNodes', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let nodeData=data.nodes;
            let total0=data.totalNumber;
            // if(data.nodes.length>0){
            //     if(selectBool){
            //         nodeId=data.nodes[0].nodeId;
            //         addRuleId=nodeId;
            //     }
            //     let pageBool=false;
            //     let nodeRulesDataAll=[];
            //     this.setState({ nodeData,total0,nodeId,pageBool,nodeRulesDataAll,addRuleId},()=>this.fetchNodeRules());
            // }else{
            //     let pageBool=true;
            //     let colorIndex="Tem";
            //     this.setState({ nodeData,total0,colorIndex,pageBool},()=>this.fetchNodeRulesAll());
            // }
             if((data.nodes.length==0||special==true)&&specialFirst==true&&(data.nodes.length==0||this.state.pageBool)){
                let pageBool=true;
                if(this.state.colorIndex!=="Tem"){
                    special=false;
                }
                let colorIndex="Tem";
                this.setState({ nodeData,total0,colorIndex,pageBool,special,selectReplace:true},()=>this.fetchNodeRulesAll());
            }else{
                //if(selectBool||this.state.specialFirst==false){
                if((selectBool||this.state.specialFirst==false)&&this.state.selectReplace){
                    nodeId=data.nodes[0].nodeId;
                    addRuleId=nodeId;
                }
                let pageBool=false;
                let specialFirst=true;
                let nodeRulesDataAll=[];
                this.setState({ nodeData,total0,nodeId,pageBool,nodeRulesDataAll,addRuleId,specialFirst,selectReplace:true},()=>this.fetchNodeRules());
            }
        }).catch((err) => console.log(err.toString()));
    }
    //获取节点规则
    fetchNodeRules(){
        //let {nodeRulesData,total1}=this.state;
        let param = new FormData();
        param.append('nodeId',this.state.nodeId);
        param.append('pageSize',this.state.size1);
        param.append('pageIndex',this.state.index1);

        fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/listRules', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
           let nodeRulesData=data.rules;
           let total1=0;
           if(this.state.selectBool){
               total1=(data.totalNumber==0?"null":data.totalNumber);
               this.handleSelectFetch(nodeRulesData);
              
           }else{
               total1=this.state.total1;
           }
           let searchTableBool=false;
           this.setState({ nodeRulesData,total1,searchTableBool});
        }).catch((err) => console.log(err.toString()));
    }
    //获取模fetchNodeRulesAll板下的所有规则
    fetchNodeRulesAll(){
        let {total1}=this.state;
        let param = new FormData();
        param.append('templateId',this.state.templateId);
        param.append('pageSize',this.state.size1);
        param.append('pageIndex',this.state.index1);

        fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/listRulesByTemplate', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
           let pageBool=true;
           let nodeRulesData=[];
           let nodeRulesDataAll=data.rules;
           //let total1=data.totalNumber;
           if(!this.state.selectBool){
                total1=data.totalNumber;
                this.handleSelectFetch(nodeRulesDataAll);
           }
           this.setState({ nodeRulesDataAll,total1,nodeRulesData,pageBool});
        }).catch((err) => console.log(err.toString()));
    }
    //过滤模板
    filterTemplates(indexCount,addTemId){
        let ruleType=this.state.ruleType;
        Object.keys(ruleType).map((index) => 
           ruleType[index].boolean=false
        );
        ruleType[indexCount].boolean=true;
        let ruleTypeId=this.state.ruleType[indexCount].id;
        let index=1;
        let searchWordTem='';
        this.setState({ruleTypeId,index,ruleType,addTemId,searchWordTem}, () =>this.fetchTemplateList());
    }
    //展示节点
    changeNode(index){
        let {searchTableBool}=this.state;
        let tem_index=index;
        let index0=1;
        let pageBool=true;
        if(this.state.isNodeDetail==false){
             this.resetSearchRule();
             searchTableBool=false;
        }
        let isNodeDetail=false;
        let templateId=this.state.templatesData[index].templateId;
        let colorIndex="Tem";
        let addRuleId=0;
        let selectBool=false;
        let index1=1;
        let pageStart=0;
        let size1=10;
        this.setState({ isNodeDetail,templateId,tem_index,pageBool,colorIndex,addRuleId,selectBool,index1,pageStart,index0,size1,searchTableBool},()=>this.joinTem());
    }
    joinTem(){
         this.fetchNodeList();
         this.fetchNodeRulesAll();
    }
    changeTemplate(){
        let pageBool=false;
        let index0=1;
        let index1=1;
        let total1=0;
        let pageStart=0;
        let isNodeDetail=true;
        let nodeData=[];
        let nodeRulesData=[];
        let nodeRulesDataAll=[];
        //this.fetchTemplateList();
        let searchTableBool=false;
        this.setState({ isNodeDetail,nodeRulesData,nodeRulesDataAll,pageBool,index1,total1,pageStart,nodeData,index0,searchTableBool},()=>this.fetchTemPage());
    }
    //展示节点规则
    nodeRules(nodeId,index){
        let pageBool=false;
        let index1=1;
        let total1=0;
        let nodeRulesDataAll=[];
        let colorIndex=index;
        let addRuleId=nodeId;
        let selectBool=true;
        let pageStart=0;
        let size1=10;
        this.resetSearchRule();
        let searchTableBool=false;
        this.setState({nodeId,index1,pageBool,index1,total1,nodeRulesDataAll,colorIndex,addRuleId,selectBool,pageStart,size1,searchTableBool},()=>this.fetchNodeRules());
    }
    //分页
    exchangePage(index,size){
        this.setState({index},()=>this.fetchTemPage());
    }
    fetchTemPage(){
        if(this.state.resultTemBool){
            this.fetchTemplateList();
        }else{
            this.searchTem();
        }
    }
    exchangePage0(index0,size){
        let colorIndex="Tem";
        let special=true;
        this.setState({index0,colorIndex,special},()=>this.fetchNodeList());
    }
    exchangePage1(index1,size1){
        let pageStart=index1-1;
        this.setState({index1,pageStart,size1},()=>{this.state.searchTableBool?this.searchRules():this.fetchRules()});
    }
    fetchRules(){
        this.state.pageBool?this.fetchNodeRulesAll():this.fetchNodeRules();
    }
    
    //编辑规则模态框
    editRuleModal(ruleId,ruleName){
        this.setState({ editRuleVisible : true ,ruleId,ruleName});
    }
    closeEditRule(){
        this.resetEdit();
        let addOpera="";
        this.setState({ editRuleVisible : false ,addOpera});
    }
    beforeEditSubmit(data){
        data.rule=this.trim(data.rule);
        if(data.rule==""){
            popup(<Snackbar message="请输入新的规则名称！"/>) 
            return;
        }
        data.ruleId=this.state.ruleId;
        data.nodeId=this.state.nodeId;
        return data;
    }
    afterEditSubmit(data){
         this.resetEdit();
         let addOpera="";
         this.setState({editRuleVisible: false,addOpera});
         data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchRules() : popup(<Snackbar
         message={data.msg}/>)
    }
    //添加模态框
    addRuleModal(){
        this.setState({ addRuleVisible : true });
    }
    closeAddRule(){
        this.resetAdd();
        let addOpera="";
        this.setState({ addRuleVisible : false,addOpera });
    }
    beforeAddSubmit(data){
         data.rule=this.trim(data.rule);
        if(data.rule==""||data.nodeId==""){
            popup(<Snackbar message="请完善输入信息！"/>) 
            return;
        }
        return data;
    }
    afterAddSubmit(data){
         data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.addSuccess(): popup(<Snackbar
         message={data.msg}/>)
    }
    addSuccess(){
        let {addOpera,index1,pageStart}=this.state; 
         this.resetAdd();
         addOpera="";
         index1=1;
         pageStart=0; 
        let searchTableBool=false;
        this.fetchTemplateList();
        //this.fetchNodeList();
        this.setState({addRuleVisible: false,addOpera,index1,pageStart,searchTableBool},()=>{this.fetchRules();this.resetSearchRule();});
    }
    //删除规则
    delRulePrompt(ruleId){
        this.setState({ruleId});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleRuleOneApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此规则？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确认删除规则
    handleRuleOneApprove(after){
        let {ruleId,index1,total1,size}=this.state;
        let param = new FormData();
            param.append('ruleId',ruleId);
         fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/deleteRule', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                if(total1%size==1&&index1==Math.ceil(total1/size) ){
                    this.setState({index1:1,pageStart:0});
                }
                // data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchRules() : popup(<Snackbar
                // message={data.msg}/>)
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.deleteRuleOneSuccess() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
         after( true );
    }
    deleteRuleOneSuccess(){
        this.setState({selectReplace:false},()=>{this.searchRules(); this.fetchTemplateList();this.fetchNodeList()});
    }
    //删除模板
    delTemPrompt(templateId){
        this.setState({templateId});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleTemApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此模板？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    handleTemApprove(after){
        let {templateId,index,total,size0}=this.state;
         let param = new FormData();
            param.append('templateId',templateId);
         fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/deleteTemplate', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                if(total%size0==1&&index==Math.ceil(total/size0) ){
                    this.setState({index:1});
                }
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchTemPage() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
         after( true );
    }
    //删除节点
      delNodePrompt(nodeId){
        this.setState({nodeId});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleNodeApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此节点？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    handleNodeApprove(after){
        let {nodeId,index0,total0,size}=this.state;
         let param = new FormData();
            param.append('nodeId',nodeId);
         fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/deleteNode', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                let colorIndex=0;
                if(total0%size==1&&index0==Math.ceil(total0/size) ){
                    this.setState({index0:1});
                }
                this.setState({colorIndex});
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchNodeList()+this.fetchTemplateList() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
         after( true );
    }
    //上传规则
    uploadRuleModal(){
        this.setState({ uploadRuleVisible: true,progressBool:false });
    }
    closeUploadRule(){
        this.resetUpload();
        this.setState({ uploadRuleVisible: false });
    }
    beforeUploadSubmit(data){
        if(this.state.progressBool==false){
            if(data.file.length==0){
                popup(<Snackbar message="请选择需要上传的文件！"/>) 
                return;
            }
            this.setState({progressBool:true});
            data.templateId=this.state.templateId;
            return data;
        }
    }
    afterUploadSubmit(data){
        data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.uploadSuccess() : popup(<Snackbar message={data.msg}/>)+this.uploadFail()
    }
    uploadSuccess(){
        this.resetUpload();
        let searchTableBool=false;
        this.fetchTemplateList();
        this.setState({uploadRuleVisible: false,searchTableBool},()=>this.joinTem());
        this.resetSearchRule();
    }
    uploadFail(){
        this.setState({progressBool: false});
    }
    trim(s){
        return s.replace(/(^\s*)|(\s*$)/g, "");
    }
    //新建模板
    addTemModal(){
        this.setState({ addTemVisible: true });
    }
    closeAddTem(){
        this.resetTem();
        this.setState({ addTemVisible: false });
    }
    beforeAddTemSubmit(data){
        data.templateName=this.trim(data.templateName);
        if(data.templateName==""||data.dicTypeId==""){
            popup(<Snackbar message="请完善输入信息！"/>) 
            return;
        }
        return data;
    }
    afterAddTemSubmit(data){
         data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.addTemSuccess() : popup(<Snackbar
         message={data.msg}/>)
    }
    addTemSuccess(){
         let index=1;
         this.resetTem();
         let searchWordTem='';
         this.setState({addTemVisible: false,index,searchWordTem});
        this.fetchTemplateList();
    }
    //新建节点
    addNodeModal(){
        this.setState({ addNodeVisible: true });
    }
    closeAddNode(){
        this.resetNode();
        this.setState({ addNodeVisible: false });
    }
    beforeAddNodeSubmit(data){
        data.nodeName=this.trim(data.nodeName);
        if(data.nodeName==""){
            popup(<Snackbar message="请输入节点名称！"/>) 
            return;
        }
        data.templateId=this.state.templateId;
        return data;
    }
    afterAddNodeSubmit(data){
         this.resetNode();
         let index0=1;
         let colorIndex=0;
         let specialFirst=false;
         this.setState({addNodeVisible: false,index0,colorIndex,specialFirst});
         data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchNodeList()+this.fetchTemplateList(): popup(<Snackbar
         message={data.msg}/>)
    }
    //导出规则
    getJsonByIdDown(){
        let {selectData}=this.state;
        if(selectData == ""){
            popup( <Snackbar message={`没有选择要导出的数据`} /> );
        }else{
            this.clearSelectData();
            window.open(`${Uniplatform.context.url}/nlap/admin/ruleMgmt/export?ruleIds=${selectData}&templateId=${this.state.templateId}`,'_self');
        } 
    }
    //导出模板或对象规则
    getAllByIdDown(){
        let {pageBool,nodeId,templateId}=this.state;
        if(pageBool){
            nodeId="";
        };
        window.open(`${Uniplatform.context.url}/nlap/admin/ruleMgmt/exportTemplateAndNode?templateId=${templateId}&nodeId=${nodeId}`,'_self');
    }
    //清空表格选中数据及状态
    clearSelectData(){
        let {nodeRulesData,nodeRulesDataAll,selectBool}=this.state;
        if(selectBool){
            nodeRulesData.map((value)=>{
                value.checked=false;
            });
        }else{
            nodeRulesDataAll.map((value)=>{
                value.checked=false;
            });
        }
        this.setState({selectData:'',selectDataId:[],nodeRulesData,nodeRulesDataAll});
    }
    //删除规则
    delSelectData(){
        let {selectData}=this.state;
        if(selectData == ""){
            popup( <Snackbar message={`没有选择要删除的数据`} /> );
            return false; 
        } 
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleRuleApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除所选规则？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确认批量删除规则
    handleRuleApprove(after){
        let {selectData,index1,total1,size}=this.state;
        let param = new FormData();
        param.append("ruleId", selectData);
        fetch( Uniplatform.context.url + '/nlap/admin/ruleMgmt/deleteRule', {
            credentials: 'same-origin',
            method: 'POST',
            body : param
            })
            .then((res) => res.json())
            .then((data) => {
                let index11=Math.ceil((total1-selectData.length)/size);
                if(index11<index1){
                    this.setState({index1:1,pageStart:0});
                }else{
                    this.setState({index1});
                }
                this.setState({selectData:'',selectDataId:[]});
                // data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchRules() : popup(<Snackbar
                // message={data.msg}/>)
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.deleteRuleSuccess() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
            });
            after( true );
    }
    deleteRuleSuccess(){
        this.setState({selectReplace:false},()=>{this.searchRules(); this.fetchTemplateList();this.fetchNodeList()});
    }
    //选择表格数据
    handleSelect(data,currentData){
        let {selectDataId,selectData,selectAllDataId}=this.state;
        if(selectAllDataId.length==data.length){
            data.map((value)=>{
                selectDataId.push(value.ruleId);
            });
            selectDataId=[...new Set(selectDataId)];
        }else if(data.length==0){
            selectAllDataId.map((value1)=>{
                selectDataId.map((value2,index)=>{
                    if(value1==value2){
                         selectDataId.splice(index,1);
                    }
                });
            });
        }else{
            if(currentData){
                if(selectDataId.indexOf(currentData.ruleId)!==-1){
                    let index=selectDataId.indexOf(currentData.ruleId);
                    selectDataId.splice(index,1);
                }else{
                    selectDataId.push(currentData.ruleId);
                }
            }
        }
        selectData = selectDataId.join(',');
        this.setState({selectData,selectDataId});

        // let selectData = [];
        // selectData = Object.keys(data).map((index) => 
        //     `${data[index].ruleId}`
        // ).join(',');
        // this.setState({selectData});
    }
    handleSelectFetch(nodeRulesData){
        let {selectDataId,selectAllDataId}=this.state;
        selectAllDataId=[];
        if(selectDataId){
            selectDataId.map((value1)=>{
                nodeRulesData.map((value2)=>{
                    if(value1==(value2.ruleId)){
                        value2.checked=true;
                    }
                });
            });
        };
        if(nodeRulesData){
            nodeRulesData.map((value)=>{
                selectAllDataId.push(value.ruleId);
            });
        };
        this.setState({selectAllDataId});
    }
    //清除回显
    formTirggerTem( trigger ) {
        this.resetTem = trigger.reset;
    }
    formTirggerNode( trigger ) {
        this.resetNode = trigger.reset;
    }
    formTirggerEdit( trigger ) {
        this.resetEdit = trigger.reset;
    }
    formTirggerAdd( trigger ) {
        this.resetAdd = trigger.reset;
    }
    formTirggerUpload( trigger ) {
        this.resetUpload = trigger.reset;
    }
    searchTirggerRule(trigger){
        this.resetSearchRule = trigger.reset;
    }
    //运算符号
    getOpera(opera){
        let dataAdd="";
        let dataEdit="";
        if(this.state.addRuleVisible){
            dataAdd = this.getValueAdd();
        }
        if(this.state.editRuleVisible){
            dataEdit = this.getValueEdit();
        }
        let addOpera=dataAdd+opera;
        let ruleName=dataEdit+opera;
        this.setState({addOpera,ruleName});
    }

    formGetterAdd( getter ) {
        this.getValueAdd = getter.value;
    }
    formGetterEdit( getter ) {
        this.getValueEdit = getter.value;
    }
    //通过关键字检索模板
    getWordInputTem(getter){
        this.getWordValueTem=getter.value;
    }
    wordSearchTem(event){
        let {resultTemBool}=this.state;
        let index=1;
        if (event.which==13){
            let searchWordTem = this.getWordValueTem();
            if(searchWordTem==''){
                resultTemBool=true;
            }else{
                resultTemBool=false;
            }
            this.setState({searchWordTem,index,resultTemBool}, () => this.searchTem());
        }
    }
    wordSearchAutoTem(data){
        let index=1;
        //let searchWordTem = this.getWordValueTem();
        //this.setState({searchWordTem});
         if(data==""){
            let resultTemBool=true;
            this.setState({searchWordTem:"",resultTemBool,index}, () => this.searchTem());
        }
    }
    searchTem(){
        let {size0,index,searchWordTem,ruleTypeId}=this.state;
        let param = new FormData();
        param.append('pageSize',size0);
        param.append('pageIndex',index);
        param.append('keyWord',searchWordTem);
        param.append('ruleTypeId',ruleTypeId);
        fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/searchTemplate', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let templatesData = data.template;
            let total=data.totalNum;
            this.setState({ templatesData,total });
        }).catch((err) => console.log(err.toString()));
    }
    searchClearTem(){
        let index=1;
        let resultTemBool=true;
        this.setState({searchWordTem:"",resultTemBool,index}, () => this.searchTem());
    }
    //通过关键字检索规则
    getWordInput(getter){
        this.getWordValue=getter.value;
    }
    wordSearch( event ) {
        let {searchTableBool,pageStart}=this.state;
        let index1=1;
        if (event.which==13){
            let searchWord = this.getWordValue();
            let size1=10;
            searchTableBool=true;
            pageStart=0;
            this.setState({searchWord,index1,size1,searchTableBool,pageStart}, () => this.searchRules());
        }
    }
     wordSearchAuto(data){
        if(data==""){
            let size1=10;
            let pageStart=0;
            let index1=1;
            this.setState({searchWord:"",size1,pageStart,index1}, () => this.searchRules());
        }
    }
    searchRules(){
        let {size1,index1,total1,searchWord,templateId,nodeId,pageBool,nodeRulesDataAll,nodeRulesData}=this.state;
        if(pageBool){
            nodeId="";
        }
        let param = new FormData();
        param.append('pageSize',size1);
        param.append('pageIndex',index1);
        param.append('keyWord',searchWord);
        param.append('templateId',templateId);
        param.append('nodeId',nodeId);
        fetch(Uniplatform.context.url + '/nlap/admin/ruleMgmt/searchRule', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            if(pageBool){
                nodeRulesDataAll=data.rules;
            }else{
                nodeRulesData=data.rules;
            }
            total1=data.totalNum;
            this.setState({nodeRulesDataAll,nodeRulesData,total1});
        }).catch((err) => console.log(err.toString()));
    }
    searchClearRule(){
        let size1=10;
        let pageStart=0;
        let index1=1;
        this.setState({searchWord:"",size1,pageStart,index1}, () => this.searchRules());
    }
    render(){
        let {templatesData,isNodeDetail,ruleType,ruleTypePart,index,total,size1,size,size0,index0,total0,tem_index,nodeRulesData,nodeRulesDataAll,index1,total1,editRuleVisible,ruleId,addRuleVisible,uploadRuleVisible,addTemVisible,addNodeVisible,nodeData,pageBool,colorIndex,ruleName,addTemId,addRuleId,addOpera,searchWordTem,resultTemBool,firstIn,progressBool} = this.state;
        return(
            <Page>
                <COMM_HeadBanner prefix=" " />
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px',margin:'0px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'910px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <NLPMenu url={'/nlap/platform/rules'}/>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="ruleManage_col">
                                <div className = "ruleManage_title" >
                                    <span className = "ruleManage_span">规则管理</span>
                                </div>
                            </Col>
                            { 
                                isNodeDetail ?
                                <div key="">
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <span className="filter_span"><label className="filter_label">类型：</label></span>
                                        {
                                            Object.keys(ruleType).map((index)=>{ 
                                                return(
                                                    <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${ruleType[index].boolean==true?"#eef1f6":""}`}}  onClick={this.filterTemplates.bind(this,index,ruleType[index].id)}>{ruleType[index].displayName}</Button> 
                                                )
                                            })
                                        }
                                        <Input placeholder="请输入要查询的词" type="search"
                                            style={{float:'right'}}
                                            value={searchWordTem}
                                            onKeyPress={ this.wordSearchTem.bind(this)}
                                            getter={ this.getWordInputTem.bind(this) }
                                            onChange={this.wordSearchAutoTem.bind(this)}
                                            onClear={this.searchClearTem.bind(this)}>
                                            <Input.Left icon="search" />
                                        </Input>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.addTemModal.bind(this)}>
                                            <i className="fa fa-plus-square-o"></i> 创建模板
                                        </Button>
                                    </Col>
                                    <div className="ruleManage_content">
                                        <Row>
                                            { 
                                                firstIn==false?
                                                    templatesData.length>0?
                                                    Object.keys(templatesData).map((index)=>{
                                                        return (
                                                            <Col style={{width:'20%'}} key={index}>
                                                                <a className='close'  style={{color:'#fff',backgroundColor:"transparent"}}  onClick={this.delTemPrompt.bind(this,templatesData[index].templateId)}><Icon icon="times" /></a>
                                                                <div className="ruleManage_content tem_background centTem" onClick={this.changeNode.bind(this,index)}>
                                                                    <p>类型：{templatesData[index].ruleType}</p>
                                                                    <Dropdown position={'bottom'}>
                                                                        <Dropdown.Trigger action="hover">
                                                                        <p>名称：{templatesData[index].templateName}</p>
                                                                        </Dropdown.Trigger>
                                                                        <Dropdown.Content>
                                                                            <div style={ { width: '155px', maxHeight:'100px',overflow:'auto' } }>
                                                                                <p style={{marginLeft:'10px',marginRight:'10px'}}>{templatesData[index].templateName}</p>
                                                                            </div>
                                                                        </Dropdown.Content>
                                                                    </Dropdown>
                                                                    <p>数量：{templatesData[index].nodeNum}</p>
                                                                </div>
                                                            </Col>
                                                        )
                                                    })
                                                    :
                                                    resultTemBool?
                                                        <span className="nullContent">该类型下无模板</span>
                                                        :
                                                        <span className="nullContent">不存在对应的模板</span>
                                                :
                                                null
                                            }
                                        </Row>
                                        <Row>
                                            <Pagination index={ index }  total={ total } size={ size0 } align='center' onChange={this.exchangePage} />
                                        </Row>
                                        <Divider />
                                    </div>
                                </div>
                                :  
                                <div key="">
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <span className="filter_span"><label className="filter_label">模板名称：</label></span>
                                        <span>{templatesData[tem_index].templateName}</span>
                                         <Button type="default" size="medium" style={{float:'right',display:`${this.state.nodeData.length>0?'none':'inline-block'}`}} onClick={this.uploadRuleModal.bind(this)}>
                                            <i className="fa fa-upload"></i> 上传规则
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} disabled={eval(`${nodeRulesData.length==0&&nodeRulesDataAll.length==0}`)}  onClick={this.getAllByIdDown.bind(this)}>
                                            <i className="fa fa-download"></i> 导出规则
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.addNodeModal.bind(this)}>
                                            <i className="fa fa-plus-square-o"></i> 创建节点
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.changeTemplate.bind(this)}>
                                            <i className="fa fa-reply"></i> 返回
                                        </Button>
                                    </Col>
                                    <div className="ruleManage_content">
                                        <Row>
                                            <div key=''>
                                                <Col style={{width:'20%'}}>
                                                    <div className="ruleManage_content tem_background centTem" style={{backgroundColor:`${colorIndex==="Tem"?"rgba(126,140,153,0.5)":""}`}} onClick={this.changeNode.bind(this,tem_index)}>
                                                        <p>类型：{templatesData[tem_index].ruleType}</p>
                                                        <Dropdown position={'bottom'}>
                                                            <Dropdown.Trigger action="hover">
                                                                <p>名称：{templatesData[tem_index].templateName}</p>
                                                            </Dropdown.Trigger>
                                                            <Dropdown.Content>
                                                                <div style={ { width: '100px', height:'50px' } }>
                                                                    {templatesData[tem_index].templateName}
                                                                </div>
                                                            </Dropdown.Content>
                                                        </Dropdown>
                                                        <p>数量：{templatesData[tem_index].nodeNum}</p>
                                                    </div>
                                                </Col>
                                                {
                                                    this.state.nodeData.length>0?
                                                    <div>
                                                        <Col style={{width:'10%',marginTop:'50px',padding:'0px',textAlign:'center'}}>
                                                            <img src="../image/rule_divider.svg" style={{width:'100%',display:'inline-block'}}/>
                                                        </Col>
                                                        <Col style={{width:'70%'}}>
                                                            <div>
                                                                {
                                                                    this.state.nodeData.map((data,index)=>{
                                                                        return(
                                                                            <Col style={{width:'20%'}} key={index}>
                                                                                <a className='close'  style={{color:'#fff',backgroundColor:"transparent"}}  onClick={this.delNodePrompt.bind(this,data.nodeId)}><Icon icon="times" /></a>
                                                                                <div className="ruleManage_content node_background" style={{backgroundColor:`${colorIndex===index?"rgba(126,140,153,0.5)":""}`}} onClick={this.nodeRules.bind(this,data.nodeId,index)}>
                                                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="centNode" style={{height:'100px',zIndex:'1000'}}>
                                                                                        <p><span style={{fontSize:'14px',width:'100%',float:'left'}}>{data.nodeName}</span></p>
                                                                                    </Col>
                                                                                </div>
                                                                            </Col>
                                                                        )
                                                                    })
                                                                }
                                                            </div>        
                                                        </Col>
                                                    </div>
                                                    :
                                                    <span className="nullContentTop">此模板不存在节点</span>
                                                }
                                            </div>
                                        </Row>
                                        <Row>
                                            <Pagination index={ index0 }  total={ total0 } size={ size } align='center' onChange={this.exchangePage0}/>
                                        </Row>
                                        <Divider />
                                    </div>
                                </div>
                            }
                        </Row>
                        <Row>
                            {
                                (nodeRulesData.length>0||nodeRulesDataAll.length>0)?
                                <div>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="ruleManage_col">
                                        <div className = "ruleManage_title" >
                                            <span className = "ruleManage_span"></span>
                                        </div>
                                    </Col>
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                        <Input placeholder="请输入要查询的词" type="search"
                                            style={{float:'left'}}
                                            onKeyPress={ this.wordSearch.bind(this)}
                                            getter={ this.getWordInput.bind(this) }
                                            onChange={this.wordSearchAuto.bind(this)}
                                            trigger={ this.searchTirggerRule.bind(this) }
                                            onClear={this.searchClearRule.bind(this)}>
                                            <Input.Left icon="search" />
                                        </Input>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.delSelectData.bind(this)} >
                                            <i className="fa fa-trash"></i> 批量删除
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.getJsonByIdDown.bind(this) }>
                                            <i className="fa fa-download"></i> 导出规则
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadRuleModal.bind(this)}>
                                            <i className="fa fa-upload"></i> 上传规则
                                        </Button>
                                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.addRuleModal.bind(this)}>
                                            <i className="fa fa-plus-square-o"></i> 添加规则
                                        </Button>
                                    </Col>
                                    <div className="ruleManage_content">
                                        <Row>
                                            <Col>
                                                <Table dataSource={ pageBool?nodeRulesDataAll:nodeRulesData } headBolder style={{color:'#54698d'}} checkable={ true } striped={true} className="table_head" onCheck={ this.handleSelect.bind(this) }>
                                                    <Column title="序号" scaleWidth = '10%' textAlign="center">
                                                        { ( value, index ) => { return Number(`${Number(this.state.pageStart)*Number(this.state.size1)+Number(index)}`) + 1; } }
                                                    </Column>
                                                    <Column title="节点名称" dataIndex="nodeName" scaleWidth = '24%'textAlign="center"></Column>
                                                    <Column title="规则名称" dataIndex="rule" scaleWidth = '24%'textAlign="center"></Column>
                                                    <Column title="创建时间"  scaleWidth = '24%'textAlign="center">
                                                        { ( value, index ) => { return <DateTime format='yyyy/MM/dd hh:mm:ss' content={value.createTime} style={{textAlign:'center'}}/> } }
                                                    </Column>
                                                    <Column title="操作" scaleWidth = '18%' textAlign="center">
                                                        { ( value ) => {
                                                            return (
                                                                <div>
                                                                    <Button type="default" size="tiny" onClick={this.editRuleModal.bind(this,value.ruleId,value.rule)} style={{marginRight:'20px'}}>
                                                                        编辑
                                                                    </Button>
                                                                    <Button type="default" size="tiny" onClick={this.delRulePrompt.bind(this,value.ruleId)}>
                                                                        删除
                                                                    </Button>
                                                                </div>
                                                            )
                                                        } }
                                                    </Column>
                                                </Table>
                                                <Divider />
                                                <Pagination index={ index1 }  total={total1} size={ size1 } align='center' onChange={this.exchangePage1} showDataSizePicker dataSizePickerList={['10','50','100','200']}/>
                                            </Col>
                                        </Row>
                                        <Divider />
                                    </div>
                                </div>
                                :
                                nodeData.length>0?
                                    <div>
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="ruleManage_col">
                                            <div className = "ruleManage_title" >
                                                <span className = "ruleManage_span"></span>
                                            </div>
                                        </Col>
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                            <Button type="default" size="medium" style={{float:'right'}} disabled onClick={this.delSelectData.bind(this)} >
                                                <i className="fa fa-trash"></i> 批量删除
                                            </Button>
                                            <Button type="default" size="medium" style={{float:'right'}} disabled onClick={this.getJsonByIdDown.bind(this)}>
                                                <i className="fa fa-download"></i> 导出规则
                                            </Button>
                                            <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadRuleModal.bind(this)}>
                                                <i className="fa fa-upload"></i> 上传规则
                                            </Button>
                                            <Button type="default" size="medium" style={{float:'right'}} onClick={this.addRuleModal.bind(this)}>
                                                <i className="fa fa-plus-square-o"></i> 添加规则
                                            </Button>
                                        </Col>
                                        <div className="ruleManage_content">
                                            {
                                                total1=="null"?
                                                <span className="nullContent">该节点无规则</span>
                                                :
                                                <span className="nullContent">该模板节点无规则</span>
                                            }
                                        </div>
                                    </div>
                                    :
                                    ""
                            }
                        </Row>
                    </Col>
                </Row>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/ruleMgmt/updateRule'}
                          async={true}
                          type="inline"
                          onSubmit={this.beforeEditSubmit}
                          onAfterSubmit={this.afterEditSubmit}
                          trigger={ this.formTirggerEdit }
                    >
                    {
                        editRuleVisible?
                        <Modal visible={ editRuleVisible } size="middle" onClose={ this.closeEditRule.bind( this ) }>
                            <ModalHeader>
                                编辑规则
                            </ModalHeader>
                            <ModalBody>
                                <FormItem>
                                    <Label>规则名称</Label>
                                    <Textarea name="rule" placeholder="请输入规则名称. . ."  rows={ 4 } value={ruleName} getter={ this.formGetterEdit }/>
                                </FormItem>
                                <FormItem>
                                    <Label>选择运算符</Label>
                                        <Select placeholder="" name="operaId" onChange={this.getOpera} showClear={ false }>
                                            <Option value="+">+ </Option>
                                            <Option value="-">- </Option>
                                            <Option value="*">* </Option>
                                            <Option value="=">=</Option>
                                            <Option value="@">@</Option>
                                            <Option value="%">% </Option>
                                            <Option value="#">#</Option>
                                            <Option value="$">$</Option>
                                            <Option value="&">&</Option>
                                            <Option value="^">^</Option>
                                            <Option value="?">?</Option>
                                            <Option value="(">(</Option>
                                            <Option value=")">) </Option>
                                        </Select>
                                </FormItem>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeEditRule.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                        :
                        ""
                    }
                </Form>
                <Form method="post"
                        action={contextUrl + '/nlap/admin/ruleMgmt/addRule'}
                        async={true}
                        type="horizontal"
                        onSubmit={this.beforeAddSubmit.bind(this)}
                        onAfterSubmit={this.afterAddSubmit.bind(this)}
                        trigger={ this.formTirggerAdd }
                    >
                    {
                        addRuleVisible?
                        <Modal visible={ addRuleVisible } size="middle" onClose={ this.closeAddRule.bind( this ) }>
                            <ModalHeader>
                                添加规则
                            </ModalHeader>
                            <ModalBody>
                                <Row>
                                    <Col size={{ normal: 8, small: 24, medium: 16, large: 16 }} style={{padding:"0px"}}>
                                        <FormItem>
                                            <Col size={{ normal: 8, small: 24, medium: 6, large: 6 }}><Label style={{wordBreak:"keep-all"}}>节点名称</Label></Col>
                                            <Col size={{ normal: 16, small: 24, medium: 18, large: 18 }}>
                                            {
                                                addRuleId==0?
                                                <Select placeholder="请输入节点名称" search={ true } name="nodeId" style={{width:"100%"}} showClear={ false }>
                                                    {
                                                        Object.keys(nodeData).map((index)=>{ 
                                                            return(
                                                                <Option value={nodeData[index].nodeId} key={index}>{nodeData[index].nodeName} </Option>
                                                            )
                                                        })
                                                    }
                                                </Select>
                                                :
                                                <Select placeholder="请输入节点名称" search={ true } name="nodeId" value={[`${addRuleId}`]} style={{width:"100%"}} showClear={ false }>
                                                    {
                                                        Object.keys(nodeData).map((index)=>{ 
                                                            return(
                                                                <Option value={nodeData[index].nodeId} key={index}>{nodeData[index].nodeName} </Option>
                                                            )
                                                        })
                                                    }
                                                </Select>
                                            }
                                            </Col>
                                        </FormItem>
                                        <FormItem>
                                            <Col size={{ normal: 8, small: 24, medium: 6, large: 6 }}><Label style={{wordBreak:"keep-all"}}>规则名称</Label></Col>
                                            <Col size={{ normal: 16, small: 24, medium: 18, large: 18 }}>
                                                <Textarea name="rule" placeholder="请输入规则名称. . ."  rows={ 4 } value={addOpera} getter={ this.formGetterAdd } style={{width:"100%"}}/>
                                            </Col>
                                        </FormItem>
                                    </Col>
                                    <Col size={{ normal: 8, small: 24, medium: 8, large: 8 }} style={{padding:"0px"}}>
                                        <FormItem style={{height:"37px"}}>
                                            <Col size={{ normal: 8, small: 24, medium: 24, large: 24 }}><Label style={{wordBreak:"keep-all",lineHeight:"37px"}}>选择运算符</Label></Col>
                                            <Col size={{ normal: 16, small: 24, medium: 24, large: 24 }}>
                                                <Select placeholder="" name="operaId" onChange={this.getOpera} style={{width:"100%"}} showClear={ false }>
                                                    <Option value="+">+ </Option>
                                                    <Option value="-">- </Option>
                                                    <Option value="*">* </Option>
                                                    <Option value="=">=</Option>
                                                    <Option value="@">@</Option>
                                                    <Option value="%">% </Option>
                                                    <Option value="#">#</Option>
                                                    <Option value="$">$</Option>
                                                    <Option value="&">&</Option>
                                                    <Option value="^">^</Option>
                                                    <Option value="?">?</Option>
                                                    <Option value="(">(</Option>
                                                    <Option value=")">) </Option>
                                                </Select>
                                            </Col>
                                        </FormItem>
                                    </Col>
                                </Row>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddRule.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                        :
                        ""
                    }
                </Form>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/ruleMgmt/uploadRule'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeUploadSubmit.bind(this)}
                          onAfterSubmit={this.afterUploadSubmit.bind(this)}
                    >
                    <Modal visible={ uploadRuleVisible } size="middle" onClose={ this.closeUploadRule.bind( this ) }>
                        <ModalHeader>
                            上传规则
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>上传文件</Label>
                                <Upload placeholder="选择文件" name="file" multiple trigger={ this.formTirggerUpload }/>
                            </FormItem>
                            {
                                progressBool?
                                <Progress type="loading" />
                                :
                                null
                            }
                            <span className="upExamples"> 1、 系统支持.xml 格式的文本文件进行上传。<br/>
                                    2、xml文件的<a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=rule.xml">示例文件</a>
                            </span>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeUploadRule.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/ruleMgmt/addTemplate'}
                          async={true}
                          type="horizontal"
                          onSubmit={this.beforeAddTemSubmit.bind(this)}
                          onAfterSubmit={this.afterAddTemSubmit.bind(this)}
                          trigger={ this.formTirggerTem }
                    >
                    <Modal visible={ addTemVisible } size="middle" onClose={ this.closeAddTem.bind( this ) }>
                        <ModalHeader>
                            新建模板
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Col size={{ normal: 8, small: 24, medium: 8, large: 8 }}><Label>模板名称</Label></Col>
                                <Col size={{ normal: 16, small: 24, medium: 16, large: 16 }}>
                                    <Input name="templateName" placeholder="请输入模板名称" />
                                </Col>
                            </FormItem>
                            <FormItem>
                                <Col size={{ normal: 8, small: 24, medium: 8, large: 8 }}><Label>模板类型</Label></Col>
                                <Col size={{ normal: 16, small: 24, medium: 16, large: 16 }}>
                                {
                                    addTemId==0?
                                    <Select placeholder="请选择模板类型" search={ true } name="dicTypeId" showClear={ false }>
                                        {
                                            Object.keys(ruleTypePart).map((index)=>{ 
                                                return(
                                                    <Option value={ruleTypePart[index].id} key={index}>{ruleTypePart[index].displayName} </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                    :
                                    <Select placeholder="请输入模板名称" search={ true } name="dicTypeId" value={[`${addTemId}`]} showClear={ false }>
                                        {
                                            Object.keys(ruleTypePart).map((index)=>{ 
                                                return(
                                                    <Option value={ruleTypePart[index].id} key={index}>{ruleTypePart[index].displayName} </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                }
                                </Col>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddTem.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/ruleMgmt/addNode'}
                          async={true}
                          type="horizontal"
                          onSubmit={this.beforeAddNodeSubmit.bind(this)}
                          onAfterSubmit={this.afterAddNodeSubmit.bind(this)}
                    >
                    <Modal visible={ addNodeVisible } size="middle" onClose={ this.closeAddNode.bind( this ) }>
                        <ModalHeader>
                            新建节点
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Col size={{ normal: 8, small: 24, medium: 8, large: 8 }}><Label>节点名称</Label></Col>
                                <Col size={{ normal: 16, small: 24, medium: 16, large: 16 }}>
                                    <Input name="nodeName" placeholder="请输入节点名称" value="" trigger={ this.formTirggerNode }/>
                                </Col>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddNode.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Footer />
            </Page>
        );
    };
}

RuleManage.UIPage = page;
export default RuleManage;