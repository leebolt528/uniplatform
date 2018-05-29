import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Cascade,
    Upload,TreeSelect,Dialog,Dropdown} from 'epm-ui';
/*import { Footer,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';*/

const contextUrl = '/uniplatform';

const page = {
    title: '功能策略管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css'
    ]
};

class StrategyManage extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            userList : [],
            addStrVisible:false,
            updateVisible:false,
            StrategiesList: [],
            siteData: [],
            dateData: [],
            taskData: [],
            unsucTaskData:[],
            isModals: false,
            isBatch: false,
            isRules: false,
            isDependency: false,
            isDic: false,
            dictionaryList: [],
            depStrList: [],
            importVisible: false
        }
        this.getFetchStrategies = this.getFetchStrategies.bind(this);
        this.handleSelect = this.handleSelect.bind( this );
        this.formUploadTirgger = this.formUploadTirgger.bind(this);
        this.beforeSubmit = this.beforeSubmit.bind(this);
        this.afterAddSubmit = this.afterAddSubmit.bind(this);
        this.afterImportSubmit = this.afterImportSubmit.bind(this);
        this.afterEditSubmit = this.afterEditSubmit.bind(this);
        this.formTirgger = this.formTirgger.bind( this );
        this.formEditTirgger = this.formEditTirgger.bind(this);
        this.handleApprove = this.handleApprove.bind( this );
        this.beforeUploadSubmit = this.beforeUploadSubmit.bind(this);
        this.beforeUpdateSubmit = this.beforeUpdateSubmit.bind(this);
    }

    componentDidMount() {
        this.getFetchStrategies();
        this.getFetchFunctions();
        this.getFetchAlgorithms();
        //this.getFetchAllStrategies();
    }

    //初始化策略列表
    getFetchStrategies(functionId){
        let param = new FormData();
        let {size,index} = this.state;
        let search = functionId ? functionId : 'all';
        param.append('pageIndex', index);
        param.append('pageSize', size);
        param.append('functionId',search);
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/strategies', {
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
                if(data.strategiesNumber == 0){
                    noData = true;
                }
                let total = data.strategiesNumber;
                let StrategiesList = [];
                data.strategyInfo.map(function (item) {
                    StrategiesList.push({
                        createTime : item.strategy.createTime,
                        //algorithmId : item.strategy.algorithmId,
                        //batchProcessing : item.strategy.batchProcessing,
                        defaultUse : item.strategy.defaultUse,
                        //functionId : item.strategy.functionId,
                        functionName : item.functionName,
                        strategyId : item.strategy.id,
                        inUsing : item.strategy.inUsing,
                        name : item.strategy.name,
                        //operation : item.strategy.operation
                        businessNames : item.businessNames,
                        nodeNames : item.nodeNames
                    });
                });
                this.setState({StrategiesList,total,noData});
            }).catch((err) => console.log(err.toString()));
    }

    //获取所有算法
    getFetchAlgorithms(){
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/algorithms/info', {
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
                let algEchoList = [];
                data.algorithmsInfo.map(function (item) {
                    algEchoList.push({
                        algorithmsId : item.id,
                        hasBatch : item.hasBatch,
                        hasDependency : item.hasDependency,
                        hasDic : item.hasDic,
                        hasModel : item.hasModel,
                        hasRule : item.hasRule
                    });
                });
                this.setState({algEchoList});
            }).catch((err) => console.log(err.toString()));
    }

    //获取所有策略（依赖策略回显用）
    /*getFetchAllStrategies(){
     fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/strategies/info', {
     credentials: 'same-origin',
     method: 'POST'
     })
     .then((res) => res.json())
     .then((data) => {
     /!*let depStrList = data.strategiesInfo.map(function(item){
     let depStr = {
     name: item.functionName,
     data:{value: item.functionName},
     children: [{
     name : item.strategy.name,
     data:{value: item.strategy.id}
     }]
     }
     return depStr;
     });
     if (data.status != 200){
     popup(<Snackbar message={data.msg}/>);
     }
     this.setState({depStrList});*!/


     let strEchoList = [];
     data.strategiesInfo.map(function (item) {
     strEchoList.push({
     functionName : item.functionName,
     strategyId : item.strategy.id
     });
     });
     if (data.status != 200){
     popup(<Snackbar message={data.msg}/>);
     }
     this.setState({strEchoList});
     }).catch((err) => console.log(err.toString()));
     }*/

    showAddModal(){
        this.setState({ addStrVisible : true });
    }

    closeAddStr(){
        this.resetAdd();
        this.setState({ addStrVisible : false, isRules : false, isBatch : false, isModals : false,
            isDic : false, isDependency : false});
    }

    exChangePagi(functionId,index,size){
        this.setState({index, size}, () => this.getFetchStrategies(functionId));
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

    //获取功能列表
    getFetchFunctions(){
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/functions?ascSort=true', {
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
                let functList = [];
                for(let i=1; i<data.functions.length;i++){
                    functList.push({
                        value: data.functions[i].id,
                        text: data.functions[i].displayName
                    });
                }
                let functionList = data.functions.map(function(item){
                    let functions = {
                        value: item.id,
                        text: item.displayName
                    }
                    return functions;
                });
                this.setState({functionList,functList});
            }).catch((err) => console.log(err.toString()));
    }

    //处理功能列表更改操作
    handleFuncChange(funcId){
        this.resetAlgorithm();
        this.reset();
        this.getAlgorithms(funcId);
        this.getDependencyStrategy(funcId);
        //this.getFetchDicTypeList();
    }
    //获取算法列表
    getAlgorithms(funcId){
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/algorithms?functionId='+funcId, {
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
                let alstatus = [];
                let algorithmsList = data.algorithms.map(function(item){
                    alstatus.push({
                        algorithmsId : item.id,
                        hasModel : item.hasModel,
                        hasBatch : item.hasBatch,
                        hasDependency : item.hasDependency,
                        hasDic : item.hasDic,
                        hasRule : item.hasRule
                    });
                    let algorithms = {
                        value: item.id,
                        text: item.displayName
                    }
                    return algorithms;
                });
                this.setState({algorithmsList:algorithmsList,alstatus});
                this.getFetchDicList(funcId);
            }).catch((err) => console.log(err.toString()));
    }
    //获取依赖策略列表
    getDependencyStrategy(funcId){
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/dependency/strategy?functionId='+funcId, {
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
                let depStrList = data.info.map(function(item){
                    let child = item.dependencyStrategys.map(function (str) {
                        let c = {
                            name : str.依赖策略名称,
                            data:{value: str.依赖策略id}
                        }
                        return c;
                    });
                    let depStr = {
                        name: item.functionName,
                        data:{value: item.functionName},
                        children: child
                    }
                    return depStr;
                });
                this.setState({depStrList});
            }).catch((err) => console.log(err.toString()));
    }

    //处理算法列表更改操作
    handleAlChange(algorithmId){
        this.resetRules();
        this.resetModels();
        this.resetDependency();
        this.resetRulesEdit();
        this.resetModelsEdit();
        this.resetDependencyEdit();
        let {alstatus,isModals,isBatch,isDependency,isDic,isRules} = this.state;
        alstatus.map(function (item) {
            if(item.algorithmsId == algorithmId){
                if(item.hasModel == 0){isModals = true;}else {isModals = false;}
                if(item.hasBatch == 0){isBatch = true;}else {isBatch = false;}
                if(item.hasDependency == 0){isDependency = true;}else {isDependency = false;}
                if(item.hasDic == 0){isDic = true;}else {isDic = false;}
                if(item.hasRule == 0){isRules = true;}else {isRules = false;}
            }
        });
        this.setState({isModals,isBatch,isDependency,isDic,isRules},()=>{this.getModels(algorithmId);this.getRules(algorithmId)});
    }
    //获取模型列表信息
    getModels(algorithmId){
        let {isModals} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/models?algorithmId='+algorithmId, {
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
                let modelsList = [];
                if(isModals){
                    modelsList.push({
                        value: '',
                        text: '该算法无模型'
                    });
                }else {
                    modelsList = data.info.map(function(item){
                        //let algorithmId = item.algorithmId;
                        let models = {
                            value: item.id,
                            text: item.name
                        }
                        return models;
                    });
                }
                this.setState({modelsList});
            }).catch((err) => console.log(err.toString()));
    }
    //查询规则列表
    getRules(algorithmId){
        let {isRules} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/rules?algorithmId='+algorithmId, {
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
                let rulesList = [];
                if(isRules){
                    rulesList.push({
                        value: '',
                        text: '该算法无规则'
                    });
                }else {
                    rulesList = data.info.map(function(item){
                        //let algorithmId = item.algorithmId;
                        let rules = {
                            value: item.id,
                            text: item.rule
                        }
                        return rules;
                    });
                }
                this.setState({rulesList});
            }).catch((err) => console.log(err.toString()));
    }

    //获取词库类型列表（普通，功能，领域）
    /*getFetchDicTypeList(){
     let dicTypeList = [];
     fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/dic/types', {
     credentials: 'same-origin',
     method: 'POST'
     })
     .then((res) => res.json())
     .then((data) => {
     dicTypeList = data.info.map(function(item){
     let dic = {
     name: item.displayName,
     data: {value: item.id}
     };
     return dic;
     });
     if (data.status != 200){
     popup(<Snackbar message={data.msg}/>);
     }
     //this.getFetchDicList(dicTypeList);
     this.getFetchDicList();
     }).catch((err) => console.log(err.toString()));
     }*/

    //查询词库列表
    getFetchDicList(functionId){
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/dics?functionId='+functionId+'&ascSort=false', {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((dictionary) => {
                if(dictionary.code){
                    popup(<Snackbar message={dictionary.message}/>);
                }else if (dictionary.status != 200){
                    popup(<Snackbar message={dictionary.msg}/>);
                }
                let dictionaryList = dictionary.info.map(function (item) {
                    let firstchild = item.dicList.map(function (list) {
                        let lastchild = list.词典列表.map(function (dic) {
                            let lc = {
                                name : dic.dicName,
                                data : {tid : dic.dicId}
                            };
                            return lc;
                        });
                        let middlechild = {
                            name : list.dicSubTypeName,
                            data : {tid : list.dicSubTypeName},
                            isLeaf : false,
                            disabled : true,
                            children : lastchild
                        };
                        return middlechild;
                    });
                    let tree = {
                        name : item.dicTypeName,
                        data : {tid : item.dicTypeName},
                        isLeaf : false,
                        disabled : true,
                        children : firstchild
                    };
                    return tree;
                });
                this.setState({dictionaryList});
            }).catch((err) => console.log(err.toString()));

    }

    showUpdateModal(strId){
        this.resetEdit();
        this.getFetchSingleStr(strId);
        this.setState({ updateVisible : true, strategyEditId: strId });
    }
    closeUpdateModal(){
        this.setState({ updateVisible : false });
    }
    //重置编辑表单
    formEditTirgger( trigger ) {
        this.resetEdit = trigger.reset;
    }

    //获取单个策略信息列表（编辑策略回显信息）
    getFetchSingleStr(strId){
        let {algEchoList,isModals,isBatch,isDependency,isDic,isRules} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/single/strategy/info?strategyId='+strId , {
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
                let strategyName_echo = data.singleStrtagyInfo.strategyName;
                //let functionName = data.singleStrtagyInfo.functionName;
                let functionId_echo = data.singleStrtagyInfo.functionId;
                //let algotithmName = data.singleStrtagyInfo.algotithmName;
                let algorithmId_echo = data.singleStrtagyInfo.algorithmId;
                //let modelName = data.singleStrtagyInfo.modelName;
                let modelId_echo = data.singleStrtagyInfo.modelId;
                //let ruleName = data.singleStrtagyInfo.ruleName;
                let ruleId_echo = data.singleStrtagyInfo.ruleId;
                let batch_echo = data.singleStrtagyInfo.batch;
                let depeStrategy = [];
                data.singleStrtagyInfo.strategyDenpendList.map(function (item) {
                    depeStrategy = [item.depengdFunctionName,item.strategyDependId];
                });
                let dicts = [];
                data.singleStrtagyInfo.dicInfo.map(function (item) {
                    dicts.push({name : item.dicName,value : item.dicId});
                });
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                this.getAlgorithms(functionId_echo);
                this.getModels(algorithmId_echo);
                this.getDependencyStrategy(functionId_echo);
                this.getRules(algorithmId_echo);
                //this.getFetchDicTypeList();
                this.getFetchDicList(functionId_echo);

                //this.handleAlChange(algorithmId_echo);
                algEchoList.map(function (item) {
                    if(item.algorithmsId == algorithmId_echo){
                        if(item.hasModel == 0){isModals = true;}else {isModals = false;}
                        if(item.hasBatch == 0){isBatch = true;}else {isBatch = false;}
                        if(item.hasDependency == 0){isDependency = true;}else {isDependency = false;}
                        if(item.hasDic == 0){isDic = true;}else {isDic = false;}
                        if(item.hasRule == 0){isRules = true;}else {isRules = false;}
                    }
                });
                this.setState({strategyName_echo,functionId_echo,algorithmId_echo,batch_echo,modelId_echo,
                    ruleId_echo,dicts,depeStrategy, isModals,isBatch,isDependency,isDic,isRules});
            }).catch((err) => console.log(err.toString()));
    }

    //表格多选
    handleSelect( data ) {
        this.setState( { selectedStr: data.map( ( key ) => key.strategyId ).join( ',' ),
            selectedDefault: data.map( ( key ) => key.defaultUse ),
            selectedInUsing: data.map( ( key ) => key.inUsing )} );
    }
    //批量删除
    batchDeleteStr(selectedStr,selectedDefault,selectedInUsing){
        this.setState({message : undefined});
        if(selectedStr.length == 0){
            popup(<Snackbar message="您未选择任何策略"/>);
        }else {
            for(let i=0; i<selectedDefault.length; i++){
                if(selectedDefault[i]){
                    this.setState({ message : '所选策略中默认策略不能删除，其余已删除成功'});
                }
            }
            for(let i=0; i<selectedInUsing.length; i++){
                if(selectedInUsing[i]){
                    this.setState({ message : "所选策略中正在被使用的策略不能删除，其余已删除成功"});
                }
            }
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
        let {functionId} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/strategy/delete?strategyId='+strId , {
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
                        this.getFetchStrategies(functionId);
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

    //将策略设为默认策略
    setDefault(strId){
        let {functionId} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/strategy/mgmt/strategy/set/default?strategyId='+strId , {
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
                        this.getFetchStrategies(functionId);
                    }
                }
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
    formUploadTirgger(trigger){
        this.resetUpload = trigger.reset;
    }
    afterImportSubmit(data){
        let {functionId} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ importVisible : false });
                this.resetUpload();
                this.getFetchStrategies(functionId);
            }
        }
    }
    //导出策略
    exportStr(selectedStr){
        if(selectedStr.length == 0){
            popup(<Snackbar message="您未选择任何策略"/>);
        }else {
            window.open(`${Uniplatform.context.url}/nlap/admin/strategy/mgmt/strategies/export?strategiesId=${selectedStr}`,'_self');
        }
    }

    beforeSubmit(data){
        let {isRules,isModals,isDic,isDependency} = this.state;
        let strategyName = data.strategyName.replace(/(^\s*)|(\s*$)/g, "");
        if(!data.functionId){
            popup(<Snackbar message="功能不能为空"/>);
            return false;
        }else if (!data.algorithmId){
            popup(<Snackbar message="算法不能为空"/>);
            return false;
        }else if (!strategyName){
            popup(<Snackbar message="策略名不能为空"/>);
            return false;
        }else if (!isRules && (!data.ruleId)){
            popup(<Snackbar message="规则不能为空"/>);
            return false;
        }else if (!isModals && (!data.modelId)){
            popup(<Snackbar message="模型不能为空"/>);
            return false;
        }else if (!isDic && (!data.dictsId)){
            popup(<Snackbar message="词库不能为空"/>);
            return false;
        }else if (!isDependency && (!data.dependencyStrategyId)){
            popup(<Snackbar message="依赖策略不能为空"/>);
            return false;
        }else {
            let depStr = data.dependencyStrategyId;
            let bodys = depStr[1] ? depStr[1] : '';
            let body = "dependencyStrategyId";
            data[body] = bodys;
            let s = 'strategyName';
            data[s] = strategyName;
            return data;
        }
    }
    afterAddSubmit(data){
        let {functionId} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.resetAdd();
                this.getFetchStrategies(functionId);
                this.setState({ addStrVisible : false, isRules : false, isBatch : false, isModals : false,
                    isDic : false, isDependency : false});
            }
        }
    }
    //重置表单
    formTirgger( trigger ) {
        this.resetAdd = trigger.reset;
    }

    beforeUpdateSubmit(data){
        let depStr = data.dependencyStrategyId;
        let bodys = depStr[1] ? depStr[1] : '';
        let body = "dependencyStrategyId";
        if(data.dictsId.length == 0){
            let dictsId = "dictsId";
            data[dictsId] = '';
        }
        data[body] = bodys;
        return data;
    }
    afterEditSubmit(data){
        let {functionId} = this.state;
        if(data.code){
            popup(<Snackbar message={data.message}/>);
        }else {
            popup(<Snackbar message={data.msg}/>);
            if(data.status == 200){
                this.setState({ updateVisible : false });
                this.getFetchStrategies(functionId);
            }
        }
    }

    //根据功能检索策略
    filterStr(functionId){
        this.setState({functionId, index : 1}, ()=>this.getFetchStrategies(functionId));
    }

    /*handleChange( value ) {
        let dicString = '';
        for(let i=0; i<value.length; i++){
            if(i == value.length-1){
                dicString += value[i];
            }else {
                dicString += value[i] + ',';
            }
        }
        this.setState({dicString});
    }*/

    //导入策略校验
    beforeUploadSubmit(data){
        if(data.files.length == 0){
            popup(<Snackbar message="文件不能为空"/>);
            return false;
        }else if(data.files.length > 1){
            popup(<Snackbar message="一次只能导入一个策略文件"/>);
        }else {
            return data;
        }
    }

    //创建策略时重置算法
    formAlgorithmTirgger(trigger){
        this.resetAlgorithm = trigger.reset;
    }

    //创建策略时重置规则
    formRulesTirgger(trigger){
        this.resetRules = trigger.reset;
    }
    //编辑策略时重置规则
    formRulesEditTirgger(trigger){
        this.resetRulesEdit = trigger.reset;
    }

    //创建策略时重置规则
    formModelsTirgger(trigger){
        this.resetModels = trigger.reset;
    }
    //编辑策略时重置规则
    formModelsEditTirgger(trigger){
        this.resetModelsEdit = trigger.reset;
    }

    //创建策略时重置依赖策略
    formDependencyTirgger(trigger){
        this.resetDependency = trigger.reset;
    }
    //编辑策略时重置依赖策略
    formDependencyEditTirgger(trigger){
        this.resetDependencyEdit = trigger.reset;
    }

    render() {
        let {addStrVisible,StrategiesList,index,total,size,functionList,algorithmsList,modelsList,isModals,dictionaryList,
            depStrList,updateVisible,selectedStr,importVisible,isRules,rulesList,isDependency,isDic,isBatch,
            strategyName_echo,functionId_echo,algorithmId_echo,batch_echo,modelId_echo,ruleId_echo,dicts,depeStrategy,
            strategyEditId,functList,selectedDefault,selectedInUsing,functionId,noData} = this.state;
        return (
            <Page>
                {/*<COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'800px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <NLPMenu url={'/nlap/platform/funcStrategy'}/>
                        </div>
                    </Col>*/}
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">功能策略</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Button type="default" size="medium" style={{margin:'15px'}} onClick={this.showAddModal.bind(this)}>
                                    <i className="fa fa-plus"></i> 添加策略
                                </Button>
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
                                        <Button onClick={this.batchDeleteStr.bind(this,selectedStr,selectedDefault,selectedInUsing)}><i className="fa fa-trash"></i> 批量删除</Button>
                                    }
                                </ButtonGroup>

                                {noData ?
                                    <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                        暂无数据
                                    </div>
                                    :
                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                        <Select dataSource={functionList} value="all" onChange={this.filterStr.bind(this)} style={{width:'300px'}} showClear={ false }/>
                                        <Divider/>
                                        <Table  striped={true} multiLine={true} checkable headBolder style={{color:'#54698d'}}
                                                onCheck={ this.handleSelect } dataSource={StrategiesList}>
                                            <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                { ( value, index ) => { return Number(`${Number(this.state.index-1)*Number(this.state.size)+Number(index)}`) + 1; } }
                                            </Column>
                                            <Column title="策略名称" scaleWidth='15%' textAlign="center">
                                                {(value)=>{
                                                    return(
                                                        <span>{value.name}&nbsp;
                                                            {value.defaultUse ?
                                                                <label style={{backgroundColor:'#8fb5dd',fontSize:'8px',padding:'2px'}}>默认</label>
                                                                : undefined}
                                                    </span>
                                                    )
                                                }}
                                            </Column>
                                            <Column dataIndex="functionName" title="所属功能" scaleWidth='15%' textAlign="center"/>
                                            <Column title="状态" scaleWidth='10%' textAlign="center">
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
                                                                            {value.businessNames.length ? '业务策略名称' : ''}
                                                                            {value.businessNames.map(function (busi,i) {
                                                                                return (<p key={i} style={{margin:'0'}}>{busi}</p>);
                                                                            })}
                                                                        </div>
                                                                        <div style={{color:'#fff',backgroundColor:'#5a5a5a'}}>
                                                                            {value.nodeNames.length ? '节点名称' : ''}
                                                                            {value.nodeNames.map(function (node,i) {
                                                                                return (<p key={i} style={{margin:'0'}}>{node}</p>);
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
                                            <Column dataIndex="user" title="创建者" scaleWidth='15%' textAlign="center"/>
                                            <Column title="创建时间" scaleWidth='15%' textAlign="center" >
                                                {(value)=>{
                                                    return this.switchTime(value.createTime);
                                                }}
                                            </Column>
                                            <Column title="操作" scaleWidth='25%' textAlign="center" >
                                                {( value )=>{
                                                    return(
                                                        <div>
                                                            { value.defaultUse ?
                                                                <Button type="default" size="tiny" disabled={true}>
                                                                    <i className="edit"></i> 编辑
                                                                </Button>
                                                                :
                                                                ( value.inUsing ?
                                                                        <Button type="default" size="tiny" disabled={true}>
                                                                            <i className="edit"></i> 编辑
                                                                        </Button>
                                                                        :
                                                                        <Button type="default" size="tiny" onClick={this.showUpdateModal.bind(this,value.strategyId)}>
                                                                            <i className="edit"></i> 编辑
                                                                        </Button>
                                                                )
                                                            }
                                                            { value.defaultUse ?
                                                                <Button type="default" size="tiny" disabled={true}>
                                                                    <i className="trash-o"></i> 删除
                                                                </Button>
                                                                :
                                                                ( value.inUsing ?
                                                                        <Button type="default" size="tiny" disabled={true}>
                                                                            <i className="trash-o"></i> 删除
                                                                        </Button>
                                                                        :
                                                                        <Button type="default" size="tiny" onClick={this.deleteStr.bind(this,value.strategyId)}>
                                                                            <i className="trash-o"></i> 删除
                                                                        </Button>
                                                                )
                                                            }
                                                            <Button type="default" size="tiny" onClick={this.exportStr.bind(this,value.strategyId)}>
                                                                <i className="trash-o"></i> 导出
                                                            </Button>
                                                            { value.defaultUse ? '' :
                                                                <Button type="default" size="tiny" onClick={this.setDefault.bind(this,value.strategyId)}>
                                                                    <i className="trash-o"></i> 设为默认
                                                                </Button>
                                                            }

                                                        </div>
                                                    )
                                                }}
                                            </Column>
                                        </Table>
                                        <Divider/>
                                        <Pagination index={index} total={total} size={size} onChange={this.exChangePagi.bind(this,functionId)}
                                                    align='center' showDataSizePicker={true} dataSizePickerList={['10','20','50','100']}/>
                                        <Divider/>
                                    </Col>
                                }


                            </div>
                        </Row>
                    </Col>
                </Row>

                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/strategy/mgmt/strategy/add'}
                      trigger={ this.formTirgger }
                      async={true}
                      onSubmit={ this.beforeSubmit }
                      onAfterSubmit={this.afterAddSubmit}
                >
                    <Modal visible={ addStrVisible } size="large" onClose={ this.closeAddStr.bind( this ) }>
                        <ModalHeader>
                            创建策略
                        </ModalHeader>
                        <ModalBody>
                            <Row>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem>
                                        <Label>功能</Label>
                                        <Select dataSource={functList} name="functionId" placeholder="请选择功能" onChange={this.handleFuncChange.bind(this)} showClear={ false }/>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem>
                                        <Label>算法</Label>
                                        <Select dataSource={algorithmsList} name="algorithmId" placeholder="请选择算法" trigger={this.formAlgorithmTirgger.bind(this)}
                                                onChange={this.handleAlChange.bind(this)} showClear={ false }/>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Divider line />
                            <Row>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem>
                                        <Label>名称</Label>
                                        <Input name="strategyName" placeholder="请输入策略名称" />
                                    </FormItem>
                                    <FormItem disabled={isRules}>
                                        <Label>规则</Label>
                                        <Select dataSource={rulesList} name="ruleId" placeholder="请选择规则" trigger={this.formRulesTirgger.bind(this)} showClear={ false }/>
                                    </FormItem>
                                    <FormItem disabled={isBatch}>
                                        <Label>批处理</Label>
                                        <Select name="batch" value="0" showClear={ false }>
                                            <Option value="0">否</Option>
                                            <Option value="1">是</Option>
                                        </Select>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem disabled={isModals}>
                                        <Label>模型</Label>
                                        <Select dataSource={modelsList} name="modelId" placeholder="请选择模型" trigger={this.formModelsTirgger.bind(this)} showClear={ false }/>
                                    </FormItem>
                                    {isDic ? <FormItem disabled><Label>词库</Label>
                                        <Select name="dictsId" placeholder="该算法无词库" value=""/>
                                    </FormItem>
                                        :
                                        <FormItem>
                                            <Label>词库</Label>
                                            <TreeSelect
                                                name="dictsId"
                                                style={{maxHeight:'50px',textAlign:'left !important'}}
                                                placeholder="请选择词库"
                                                dataSource={ dictionaryList }
                                                //value={dicString}
                                                dataValueMapper={ 'tid' }
                                                multiple
                                                trigger={ ( trigger ) => {
                                                    this.reset = trigger.reset;
                                                } }
                                                //onChange={ this.handleChange.bind( this ) }
                                            />
                                        </FormItem>
                                    }

                                    {isDependency ? <FormItem disabled><Label>依赖策略</Label>
                                        <Select name="dependencyStrategyId" placeholder="该算法无依赖策略" value=""/></FormItem> :
                                        <FormItem>
                                            <Label>依赖策略</Label>
                                            <Cascade name="dependencyStrategyId" dataSource={depStrList} placeholder="请选择依赖策略" dataValueMapper="value"
                                                     separator="/" trigger={this.formDependencyTirgger.bind(this)}/>
                                        </FormItem>
                                    }
                                </Col>
                            </Row>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddStr.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/strategy/mgmt/strategy/edit'}
                      trigger={ this.formEditTirgger }
                      async={true}
                      onSubmit={ this.beforeUpdateSubmit }
                      onAfterSubmit={this.afterEditSubmit}
                >
                    <Modal visible={ updateVisible } size="large" onClose={ this.closeUpdateModal.bind( this ) }>
                        <ModalHeader>
                            编辑策略
                        </ModalHeader>
                        <ModalBody>
                            <Input style={{ display:'none' }} name="strategyId" value={strategyEditId}/>
                            <Row>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem disabled={true}>
                                        <Label>功能</Label>
                                        <Select dataSource={functionList} name="functionId" value={functionId_echo} />
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem>
                                        <Label>算法</Label>
                                        <Select dataSource={algorithmsList} name="algorithmNewId" value={algorithmId_echo} placeholder="请选择算法"
                                                onChange={this.handleAlChange.bind(this)} showClear={ false }/>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Divider line />
                            <Row>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem>
                                        <Label>名称</Label>
                                        <Input name="strategyNewName" value={strategyName_echo} placeholder="请输入策略名称" />
                                    </FormItem>
                                    <FormItem disabled={isRules}>
                                        <Label>规则</Label>
                                        <Select dataSource={rulesList} name="ruleId" value={ruleId_echo} placeholder="请选择规则" trigger={this.formRulesEditTirgger.bind(this)} showClear={ false }/>
                                    </FormItem>
                                    <FormItem disabled={isBatch}>
                                        <Label>批处理</Label>
                                        <Select name="batch" value={batch_echo ? '1' : '0'} showClear={ false }>
                                            <Option value="0">否</Option>
                                            <Option value="1">是</Option>
                                        </Select>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                    <FormItem disabled={isModals}>
                                        <Label>模型</Label>
                                        <Select dataSource={modelsList} name="modelId" value={modelId_echo} placeholder="请选择模型"
                                                trigger={this.formModelsEditTirgger.bind(this)} showClear={ false }/>
                                    </FormItem>
                                    {isDic ? <FormItem disabled><Label>词库</Label>
                                        <Select name="dictsId" placeholder="该算法无词库" value=""/>
                                    </FormItem>
                                        :
                                        <FormItem>
                                            <Label>词库</Label>
                                            <TreeSelect
                                                name="dictsId"
                                                style={{maxHeight:'50px'}}
                                                placeholder="请选择词库"
                                                dataSource={ dictionaryList }
                                                value={dicts}
                                                //defaultValue={dicts}
                                                dataValueMapper={ 'tid' }
                                                multiple={ true }
                                                //onChange={ this.handleChange.bind( this ) }
                                            />
                                        </FormItem>
                                    }
                                    {isDependency ? <FormItem disabled><Label>依赖策略</Label>
                                        <Select name="dependencyStrategyId" placeholder="该算法无依赖策略" value=""/></FormItem> :
                                        <FormItem>
                                            <Label>依赖策略</Label>
                                            <Cascade name="dependencyStrategyId" dataSource={depStrList} placeholder="请选择依赖策略" dataValueMapper="value"
                                                     separator="/" value={depeStrategy} defaultValue={depeStrategy} trigger={this.formDependencyEditTirgger.bind(this)}/>
                                        </FormItem>
                                    }
                                </Col>
                            </Row>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeUpdateModal.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/strategy/mgmt/strategies/import'}
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={this.beforeUploadSubmit}
                      onAfterSubmit={this.afterImportSubmit}
                >
                    <Modal visible={ importVisible } size="medium" onClose={ this.closeImportStr.bind( this ) }>
                        <ModalHeader>
                            导入策略
                        </ModalHeader>
                        <ModalBody>
                            <div style={{maxHeight:'300px',overflow:'auto'}}>
                                <FormItem>
                                    <Label>文件上传</Label>
                                    <Upload limit={1} placeholder=" 上传文件" name="files" multiple={true} trigger={ this.formUploadTirgger }/>
                                </FormItem>
                                <Divider/>
                                <span style={{ float:"left",textAlign:"left",marginLeft:"40px"}}> 1、 系统支持.xml格式的文件进行上传。<br/>
                                    2、.xml文件的<a href="/uniplatform/nlap/admin/file/downloadTemplate?fileName=strategy.xml">示例文件</a>
                            </span>
                            </div>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeImportStr.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                {/*<Footer />*/}
            </Page>
        );
    }

}
StrategyManage.UIPage = page;
export default StrategyManage;

