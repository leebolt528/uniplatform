import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,popup,Snackbar,Pagination,Dialog,Upload,RadioGroup,Radio,
    Dropdown,Progress} from 'epm-ui';
import { Footer,Com_Menu,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '任务管理',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dicManage.min.css'
    ]
};

class TaskManage extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            index_data:1,
            size_data:10,
            total_data:0,
            index_plugin:1,
            size_plugin:10,
            total_plugin:0,
            businessList:[],
            pluginList:[],
            addDataSourceVisible: false,
            editDataSourceVisible: false,
            addPluginVisible: false,
            editPluginVisible: false,
            addTaskVisible: false,
            editTaskVisible: false,
            tag: 'task',
            dataSourceTypeList: [],
            dataTypeList: [],
            pluginTypeList: [],
            taskExecuteTypeList: [],
            dataSourceList: [],
            showPlugin: false,
            showEditPlugin: false,
            showExeTime: false,
            classHtml: [],
            classEditHtml: [],
            progressBool: false
        }
        this.getFetchTaskList = this.getFetchTaskList.bind(this);
        this.handleDataSelect = this.handleDataSelect.bind( this );
        this.handlePluginSelect = this.handlePluginSelect.bind(this);
        this.handleTaskSelect = this.handleTaskSelect.bind(this);
        this.formAddDataSourceTirgger = this.formAddDataSourceTirgger.bind(this);
        this.formEditDataSourceTirgger = this.formEditDataSourceTirgger.bind(this);
        this.formAddPluginTirgger = this.formAddPluginTirgger.bind(this);
        this.formEditPluginTirgger = this.formEditPluginTirgger.bind(this);
        this.formFileTirgger = this.formFileTirgger.bind(this);
        this.formAddTaskTirgger = this.formAddTaskTirgger.bind(this);
        this.formEditTaskTirgger = this.formEditTaskTirgger.bind(this);
        this.afterAddDataSourceSubmit = this.afterAddDataSourceSubmit.bind(this);
        this.afterEditDataSourceSubmit = this.afterEditDataSourceSubmit.bind(this);
        this.afterAddPluginSubmit = this.afterAddPluginSubmit.bind(this);
        this.afterEditPluginSubmit = this.afterEditPluginSubmit.bind(this);
        this.afterAddTaskSubmit = this.afterAddTaskSubmit.bind(this);
        this.afterEditTaskSubmit = this.afterEditTaskSubmit.bind(this);
        this.beforeAddDataSourceSubmit = this.beforeAddDataSourceSubmit.bind(this);
        this.beforeEditDataSourceSubmit = this.beforeEditDataSourceSubmit.bind(this);
        this.beforeAddTaskSubmit = this.beforeAddTaskSubmit.bind(this);
        this.beforeEditTaskSubmit = this.beforeEditTaskSubmit.bind(this);
        this.beforeAddPluginSubmit = this.beforeAddPluginSubmit.bind(this);
        this.beforeEditPluginSubmit = this.beforeEditPluginSubmit.bind(this);
        this.formGetter = this.formGetter.bind(this);
        this.formEditGetter = this.formEditGetter.bind(this);
        this.formTirgger = this.formTirgger.bind(this);
        this.formEditTirgger = this.formEditTirgger.bind(this);
        this.handleValue = this.handleValue.bind(this);
        this.handleEditValue = this.handleEditValue.bind(this);
        this.formNewFileTirgger = this.formNewFileTirgger.bind(this);
    }

    componentDidMount() {
        this.getFetchTaskList();
        this.getFetchDataSourceList();
        this.getFetchPluginList();
    }

    //初始化任务列表
    getFetchTaskList(){
        let param = new FormData();
        let {index,size} = this.state;
        param.append('pageNumber', index);
        param.append('pageSize', size);
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/task/list', {
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
                let total = data.count;
                let taskTable = [];
                data.result.map(function(item){
                    taskTable.push({
                        taskId : item.task.id,
                        taskName : item.task.name,
                        taskType : item.task.predefined,
                        dataSource : item.dataSource.name,
                        dataSourceId : item.dataSource.id,
                        business : item.business.name,
                        businessId : item.business.id,
                        plugin : item.plugin.name,
                        pluginId : item.plugin.id,
                        processedDataOperation : item.processedDataOperation.displayName,
                        processedDataOperationId : item.processedDataOperation.id,
                        executeType : item.taskExecuteType.displayName,
                        executeTypeId : item.taskExecuteType.id,
                        executeTime : item.task.executeTime,
                        createTime : item.task.createTime,
                        updateTime : item.task.updateTime,
                        taskStatus : item.task.taskStatus
                    });
                });
                this.setState({taskTable,total});
            }).catch((err) => console.log(err.toString()));
    }

    //初始化数据源列表
    getFetchDataSourceList(){
        let param = new FormData();
        let {index_data,size_data} = this.state;
        param.append('pageIndex', index_data);
        param.append('pageSize', size_data);
        param.append('searchWord','');
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/dataSource/dataList', {
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
                let total_data = data.count;
                let dataSourceTable = [];
                data.result.map(function(item){
                    dataSourceTable.push({
                        dsId : item.dataSource.id,
                        dsName : item.dataSource.name,
                        dsIp : item.dataSource.ip,
                        dsPort : item.dataSource.port,
                        dsPath : item.dataSource.path,
                        dsDataSourceType : item.dataSourceType.displayName,
                        dsDataSourceTypeId : item.dataSourceType.id,
                        dsDataType : item.dataType.displayName,
                        dsDataTypeId : item.dataType.id,
                        dsCreateTime : item.dataSource.createTime,
                        dsUpdateTime : item.dataSource.updateTime,
                        dsStatus : item.dataSource.status,
                        dsUserName : item.dataSource.username,
                        dsPassWord : item.dataSource.password,
                        tasks : item.tasks
                    });
                });
                this.setState({dataSourceTable,total_data});
            }).catch((err) => console.log(err.toString()));
    }

    //初始化PLUGIN列表
    getFetchPluginList(){
        let param = new FormData();
        let {index_plugin,size_plugin} = this.state;
        param.append('pageIndex', index_plugin);
        param.append('pageSize', size_plugin);
        param.append('searchWord','');
        param.append('ascSort',false);
        fetch(Uniplatform.context.url + '/nlap/admin/plugin/list', {
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
                let total_plugin = data.count;
                let pluginTable = [];
                data.result.map(function (item) {
                    pluginTable.push({
                        pluginId : item.plugin.id,
                        pluginName : item.plugin.name,
                        fileName : item.plugin.fileName,
                        uploadTime : item.plugin.uploadTime,
                        updateTime : item.plugin.updateTime,
                        pluginStatus : item.plugin.status,
                        classMap : item.plugin.classMap,
                        savePath : item.plugin.savePath,
                        tasks : item.tasks
                    });
                });
                this.setState({pluginTable,total_plugin});
            }).catch((err) => console.log(err.toString()));
    }

    //获取数据源列表
    getFetchDataSource(){
        let {dataSourceList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/task/dataSource/list', {
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
                dataSourceList = data.result.map(function(item){
                    let dst = {
                        value: item.id,
                        text: item.name
                    }
                    return dst;
                });
                this.setState({dataSourceList});
            }).catch((err) => console.log(err.toString()));
    }

    //获取原数据处理方式列表
    getFetchDataProcessType(){
        let {dataProcessTypeList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/task/dataProcessType/list', {
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
                dataProcessTypeList = data.result.map(function(item){
                    let process = {
                        value: item.id,
                        text: item.displayName
                    }
                    return process;
                });
                this.setState({dataProcessTypeList});
            }).catch((err) => console.log(err.toString()));
    }

    //获取数据源类型列表
    getFetchDataSourceType(){
        let {dataSourceTypeList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/dataSource/dsType/list', {
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
                dataSourceTypeList = data.result.map(function(item){
                    let dst = {
                        value: item.id,
                        text: item.displayName
                    }
                    return dst;
                });
                this.setState({dataSourceTypeList});
            }).catch((err) => console.log(err.toString()));
    }

    //获取数据源内的数据类型列表
    getFetchDataType(){
        let {dataTypeList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/dataSource/dataType/list', {
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
                dataTypeList = data.result.map(function(item){
                    let dt = {
                        value: item.id,
                        text: item.displayName
                    }
                    return dt;
                });
                this.setState({dataTypeList});
            }).catch((err) => console.log(err.toString()));
    }

    //taskType为预定义类型时候：查询和数据源绑定的业务策略
    getFetchBusiness(){
        let {businessList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/task/business', {
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
                businessList = data.result.map(function(item){
                    let bis = {
                        value: item.id,
                        text: item.name
                    }
                    return bis;
                });
                this.setState({businessList});
            }).catch((err) => console.log(err.toString()));
    }

    //taskType为自定义类型时候，查询plugin
    getFetchPlugin(){
        let {pluginList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/task/plugin', {
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
                pluginList = data.result.map(function(item){
                    let plu = {
                        value: item.id,
                        text: item.name
                    }
                    return plu;
                });
                this.setState({pluginList});
            }).catch((err) => console.log(err.toString()));
    }

    //获取执行类型列表
    getFetchTaskExecuteType(){
        let {taskExecuteTypeList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/task/taskExecuteType/list', {
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
                taskExecuteTypeList = data.result.map(function(item){
                    let plugin = {
                        value: item.id,
                        text: item.displayName
                    }
                    return plugin;
                });
                this.setState({taskExecuteTypeList});
                this.taskExecuteType();
            }).catch((err) => console.log(err.toString()));
    }

    //获取PLUGIN类型列表
    getFetchPluginType(){
        let {pluginTypeList} = this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/plugin/pluginType/list', {
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
                pluginTypeList = data.result.map(function(item){
                    let plugin = {
                        value: item.name,
                        text: item.name
                    }
                    return plugin;
                });
                this.setState({pluginTypeList});
            }).catch((err) => console.log(err.toString()));
    }




    exChangePagi(index,size){
        this.setState({index, size}, () => this.getFetchTaskList());
    }
    exChangePluginPagi(index_plugin,size_plugin){
        this.setState({index_plugin,size_plugin}, () => this.getFetchPluginList());
    }
    exChangeDataPagi(index_data,size_data){
        this.setState({index_data,size_data}, () => this.getFetchDataSourceList());
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

    //任务表格多选
    handleTaskSelect( data ) {
        this.setState( { selectedTask: data.map( ( key ) => key.taskId ).join( ',' ),
            selectedTaskStatus: data.map( ( key ) => key.taskStatus ).join( ',' )} );
    }
    //批量删除任务
    deleteTask(task,status){
        this.setState({ messageTask : undefined });
        if(task.length == 0){
            popup(<Snackbar message="您未选择任何任务"/>);
        }else if((status.length ==1) &&(status != 0)){
            popup(<Snackbar message="该任务正在被使用"/>);
        }else if(status.length > 1){
            let sta = status.split(',');
            for(let i=0; i<sta.length; i++){
                if(sta[i] != 0){
                    this.setState({ messageTask : "所选任务中正在被使用的任务不能删除，其余已删除成功"});
                }
            }
            this.delTask(task);
        }else {
            this.delTask(task);
        }
    }
    //删除任务
    delTask(task){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleTaskApprove.bind(this,task)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该任务?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleTaskApprove(task,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/task/deletes?ids='+task , {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                let {messageTask} = this.state;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    if (data.status == 200){
                        if(messageTask){
                            popup(<Snackbar message={messageTask}/>);
                        }else {
                            popup(<Snackbar message={data.msg}/>);
                        }
                        this.setState({ index : 1 });
                        this.getFetchTaskList();
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

    //数据源表格多选
    handleDataSelect( data ) {
        this.setState( { selectedData: data.map( ( key ) => key.dsId ).join( ',' ) ,
            selectedDataStatus: data.map( ( key ) => key.dsStatus ).join( ',' )} );
    }
    //批量删除数据源
    deleteDataSource(dataSource,status){
        this.setState({ messageData : undefined});
        if(dataSource.length == 0){
            popup(<Snackbar message="您未选择任何数据源"/>);
        }else if((status.length ==1) &&(status != 0)){
            popup(<Snackbar message="该数据源正在被使用"/>);
        }else if(status.length > 1){
            let sta = status.split(',');
            for(let i=0; i<sta.length; i++){
                if(sta[i] != 0){
                    this.setState({ messageData : "所选数据源中正在被使用的数据源不能删除，其余已删除成功"});
                }
            }
            this.deleteSource(dataSource);
        }else {
            this.deleteSource(dataSource);
        }
    }
    //删除数据源
    deleteSource(dataSource){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleDataApprove.bind(this,dataSource)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该数据源?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handleDataApprove(dataSource,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/dataSource/deletes?ids='+dataSource , {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                let {messageData} = this.state;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    if (data.status == 200){
                        if(messageData){
                            popup(<Snackbar message={messageData}/>);
                        }else {
                            popup(<Snackbar message={data.msg}/>);
                        }
                        this.setState({ index_data : 1 });
                        this.getFetchDataSourceList();
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

    //PLUGIN表格多选
    handlePluginSelect( data ) {
        this.setState( { selectedPlugin: data.map( ( key ) => key.pluginId ).join( ',' ),
            selectedPluginStatus: data.map( ( key ) => key.pluginStatus ).join( ',' ) } );
    }
    //批量删除PLUGIN
    deletePlugin(plugin,status){
        this.setState({messagePlugin : undefined});
        if(plugin.length == 0){
            popup(<Snackbar message="您未选择任何PLUGIN"/>);
        }else if((status.length ==1) &&(status != 0)){
            popup(<Snackbar message="该PLUGIN正在被使用"/>);
        }else if(status.length > 1){
            let sta = status.split(',');
            for(let i=0; i<sta.length; i++){
                if(sta[i] != 0){
                    this.setState({ messagePlugin : "所选PLUGIN中正在被使用的PLUGIN不能删除，其余已删除成功"});
                }
            }
            this.delPlugin(plugin);
        }else {
            this.delPlugin(plugin);
        }
    }
    //删除PLUGIN
    delPlugin(plugin){
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handlePluginApprove.bind(this,plugin)
        };
        popup(<Dialog style={{top:'15%'}}
                      message="确定要删除该PLUGIN?"
                      type="alert"
                      showCancelBtn
                      cancelBtn={ cancelBtn }
                      showApproveBtn
                      approveBtn = { approveBtn }
        />);
    }
    handlePluginApprove(plugin,after) {
        fetch(Uniplatform.context.url + '/nlap/admin/plugin/deletes?ids='+plugin , {
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res) => res.json())
            .then((data) => {
                let {messagePlugin} = this.state;
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    if (data.status == 200){
                        if(messagePlugin){
                            popup(<Snackbar message={messagePlugin}/>);
                        }else {
                            popup(<Snackbar message={data.msg}/>);
                        }
                        this.setState({ index_plugin : 1 });
                        this.getFetchPluginList();
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


    //添加任务
    addTaskModal(){
        this.getFetchDataSource();
        this.getFetchBusiness();
        this.getFetchTaskExecuteType();
        this.getFetchDataProcessType();
        this.getFetchPlugin();
        this.setState({ addTaskVisible : true });
    }
    closeAddTask(){
        this.resetAddTask();
        this.setState({ addTaskVisible : false, showPlugin : false, showExeTime : false });
    }
    formAddTaskTirgger(trigger){
        this.resetAddTask = trigger.reset;
    }
    beforeAddTaskSubmit(data){
        let {showExeTime,showPlugin,taskExecuteTypeList} = this.state;
        let taskName = data.taskName.replace(/(^\s*)|(\s*$)/g, "");
        if(!taskName){
            popup(<Snackbar message="任务名称不能为空"/>);
            return false;
        }else if(!data.dataSourceId){
            popup(<Snackbar message="数据源不能为空"/>);
            return false;
        }else if((!data.businessId) && (data.predefined == 'true')){
            popup(<Snackbar message="业务策略不能为空"/>);
            return false;
        }else if(data.predefined == 'false' && (!data.pluginId)){
            popup(<Snackbar message="plugin不能为空"/>);
            return false;
        }else if(showExeTime && (!data.executeTime.replace(/(^\s*)|(\s*$)/g, ""))){
            popup(<Snackbar message="执行时间不能为空"/>);
            return false;
        }else if(!data.processedDataOperationId){
            popup(<Snackbar message="数据处理方式不能为空"/>);
            return false;
        }else {
            if(data.predefined == 'true'){
                let pluginId = 'pluginId';
                data[pluginId] = '';
            }else {
                let businessId = 'businessId';
                data[businessId] = '';
            }
            if(data.executeType == taskExecuteTypeList[0].value){
                let executeTime = 'executeTime';
                data[executeTime] = '';
            }
            let ta = 'taskName';
            data[ta] = taskName;
            let ex = 'executeTime';
            data[ex] = data.executeTime.replace(/(^\s*)|(\s*$)/g, "");
            return data;
        }
    }
    afterAddTaskSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.resetAddTask();
                this.getFetchTaskList();
                this.setState({ addTaskVisible : false, showPlugin : false, showExeTime : false });
            }
        }
    }

    formEditTaskTirgger(trigger){
        this.resetEditTask = trigger.reset;
    }
    //编辑任务
    editTaskModal(task){
        this.resetEditTask();
        this.getFetchDataSource();
        this.getFetchBusiness();
        this.getFetchTaskExecuteType();
        this.getFetchDataProcessType();
        this.getFetchPlugin();
        let {taskTable,showEditPlugin} = this.state;
        let taskId,taskName,taskType,dataSourceId,businessId,pluginId_task,processedDataOperationId,executeType,
            executeTime;
        taskTable.map(function (item) {
            if(item.taskId == task){
                taskId = item.taskId;
                taskName = item.taskName;
                taskType = item.taskType;
                dataSourceId = item.dataSourceId;
                businessId = item.businessId;
                pluginId_task = item.pluginId;
                processedDataOperationId = item.processedDataOperationId;
                executeType = item.executeType;
                executeTime = item.executeTime;
            }
        });
        if(taskType){
            showEditPlugin = false;
        }else {
            showEditPlugin = true;
        }
        this.setState({ taskId,taskName,taskType,dataSourceId,businessId,pluginId_task,processedDataOperationId,
            executeType,executeTime,showEditPlugin,editTaskVisible : true });
    }
    beforeEditTaskSubmit(data){
        let {showEditPlugin} = this.state;
        let taskName = data.taskName.replace(/(^\s*)|(\s*$)/g, "");
        if(!taskName){
            popup(<Snackbar message="任务名称不能为空"/>);
            return false;
        }else if(showEditPlugin=='false' && (!data.pluginId)){
            popup(<Snackbar message="plugin不能为空"/>);
            return false;
        }else {
            if(data.predefined == 'true'){
                let pluginId = 'pluginId';
                data[pluginId] = '';
                return data;
            }else {
                let businessId = 'businessId';
                data[businessId] = '';
                let ta = 'taskName';
                data[ta] = taskName;
                return data;
            }
        }
    }
    afterEditTaskSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ editTaskVisible : false });
                this.getFetchTaskList();
            }
        }
    }
    closeEditTask(){
        this.setState({ editTaskVisible : false});
    }


    //添加PLUGIN
    addPluginModal(){
        this.getFetchPluginType();
        this.setState({ addPluginVisible : true });
    }
    closeAddPlugin(){
        this.resetAddPlugin();
        this.resetFile();
        this.setState({ addPluginVisible : false, classHtml : [] });
    }
    //添加plugin时添加类
    formGetter(getter) {
        this.getValue = getter.value;
    }
    //重置CLASSNAME
    formTirgger(trigger){
        this.resetClassName = trigger.reset;
    }
    //点击添加类
    handleValue() {
        let {pluginTypeList,classHtml} = this.state;
        const class_data = this.getValue().replace(/(^\s*)|(\s*$)/g, "");
        const plugin_data = document.getElementById('selectPlugin').value;
        if(!class_data){
            popup(<Snackbar message="类名不能为空"/>);
        }else if(!plugin_data){
            popup(<Snackbar message="每种plugin只能加一个"/>);
        }else {
            let pluginName = '';
            pluginTypeList.map(function (item,index) {
                if(item.text == plugin_data){
                    pluginName = item.text;
                    pluginTypeList.splice(index,1);
                }
            });
            classHtml.push(
                <span key={pluginName} onClick={this.removeClass.bind(this,pluginName)} style={{padding:'2px',margin:'2px',backgroundColor:'#cfdef2'}}>
                {class_data} : {pluginName}
            </span>
            );
            this.setState({classHtml,pluginTypeList});
            this.resetClassName();
        }
    }
    //添加plugin时移除类
    removeClass(pluginName){
        let {pluginTypeList,classHtml} = this.state;
        classHtml.map(function (item,index) {
            if(item.key == pluginName){
                classHtml.splice(index,1);
                pluginTypeList.push({
                    text:pluginName,
                    value:pluginName
                });
            }
        });
        this.setState({classHtml,pluginTypeList});
    }
    beforeAddPluginSubmit(data){
        let {classHtml} = this.state;
        let classNames = '';
        let pluginName = data.pluginName.replace(/(^\s*)|(\s*$)/g, "");
        if(!pluginName){
            popup(<Snackbar message="plugin名不能为空"/>);
        }else if(classHtml.length == 0){
            popup(<Snackbar message="请选择至少一种plugin类型"/>);
        }else if(data.file.length == 0){
            popup(<Snackbar message="文件不能为空"/>);
        }else {
            let suffix = data.file[0].name.split('.');
            if(suffix[suffix.length-1] != 'jar'){
                popup(<Snackbar message="文件只能是.jar格式的文件"/>);
            }else {
                this.setState({progressBool : true});
                classHtml.map(function (item) {
                    classNames += item.props.children[0]+':'+item.key+';';
                });
                let body = 'classNames';
                data[body] = classNames;
                let p = 'pluginName';
                data[p] = pluginName;
                return data;
            }
        }
    }
    //重置表单
    formAddPluginTirgger(trigger){
        this.resetAddPlugin = trigger.reset;
    }
    formFileTirgger(trigger){
        this.resetFile = trigger.reset;
    }
    afterAddPluginSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ addPluginVisible : false , classHtml : [] });
                this.resetAddPlugin();
                this.resetFile();
                this.getFetchPluginList();
            }
        }
        this.setState({progressBool : false});
    }

    //添加plugin时添加类
    formEditGetter(getter) {
        this.getEditValue = getter.value;
    }
    //编辑plugin时点击添加类
    handleEditValue() {
        let {pluginTypeList,classEditHtml} = this.state;
        const class_edit_data = this.getEditValue().replace(/(^\s*)|(\s*$)/g, "");
        const plugin_data = document.getElementById('selectPlugin').value;
        if(!class_edit_data){
            popup(<Snackbar message="类名不能为空"/>);
        }else if(!plugin_data){
            popup(<Snackbar message="每种plugin只能加一个"/>);
        }else {
            let pluginName = '';
            pluginTypeList.map(function (item,index) {
                if(item.text == plugin_data){
                    pluginName = item.text;
                    pluginTypeList.splice(index,1);
                }
            });
            classEditHtml.push(
                <span key={pluginName} onClick={this.removeEditClass.bind(this,pluginName)}
                      style={{padding:'2px',margin:'2px',backgroundColor:'#cfdef2'}}>
                {class_edit_data} : {pluginName}
            </span>
            );
            this.setState({classEditHtml,pluginTypeList});
            this.resetEditClassName();
        }
    }
    //编辑plugin时移除类
    removeEditClass(pluginName){
        let {pluginTypeList,classEditHtml} = this.state;
        classEditHtml.map(function (item,index) {
            if(item.key == pluginName){
                classEditHtml.splice(index,1);
                pluginTypeList.push({
                    text:pluginName,
                    value:pluginName
                });
            }
        });
        this.setState({classEditHtml,pluginTypeList});
    }
    //重置CLASSNAME
    formEditTirgger(trigger){
        this.resetEditClassName = trigger.reset;
    }
    formEditPluginTirgger(trigger){
        this.resetEditPlugin = trigger.reset;
    }
    formNewFileTirgger (trigger){
        this.resetNewFile = trigger.reset;
    }
    //编辑plugin回显
    editPlugin(plugin){
        fetch(Uniplatform.context.url + '/nlap/admin/plugin/pluginType/list', {
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
                let pluginTypeList = data.result.map(function(item){
                    let plugin = {
                        value: item.name,
                        text: item.name
                    }
                    return plugin;
                });
                let {pluginTable} = this.state;
                let pluginId,pluginName,fileName;
                let classEditHtml = [];
                let clsName = [],pluName = [];
                pluginTable.map(function (item) {
                    if(item.pluginId == plugin){
                        pluginId = item.pluginId;
                        pluginName = item.pluginName;
                        fileName = item.fileName;
                        Object.keys(item.classMap).map(function (cls,index) {
                            //cls +':'+  item.classMap[cls] +';'
                            if(index == 0){
                                if(pluginTypeList[0].text == item.classMap[cls]){
                                    pluginTypeList.splice(0,1);
                                }else{
                                    pluginTypeList.splice(1,1);
                                }
                            }else if(index == 1){
                                pluginTypeList = [];
                            }
                            clsName.push(cls);
                            pluName.push(item.classMap[cls]);
                        });

                    }
                });
                for(let i=0; i<clsName.length; i++){
                    classEditHtml.push(
                        <span key={pluName[i]} onClick={this.removeEditClass.bind(this,pluName[i])}
                              style={{padding:'2px',margin:'2px',backgroundColor:'#cfdef2'}}>
                        {clsName[i]} : {pluName[i]}
                    </span>
                    );
                }
                this.setState({ pluginId,pluginName,fileName,classEditHtml,pluginTypeList,
                    editPluginVisible : true });
            }).catch((err) => console.log(err.toString()));
    }
    //编辑plugin提交数据
    beforeEditPluginSubmit(data){
        let {classEditHtml} = this.state;
        let classNames = '';
        let suffix = [];
        if(data.file){
            data.file.map(function (item) {
                suffix = item.name.split('.');
            });
        }
        let pluginName = data.pluginName.replace(/(^\s*)|(\s*$)/g, "");
        if(!pluginName){
            popup(<Snackbar message="plugin名不能为空"/>);
        }else if(classEditHtml.length == 0){
            popup(<Snackbar message="请选择至少一种plugin类型"/>);
        }else if((suffix.length!=0) && (suffix[suffix.length-1] != 'jar')){
            popup(<Snackbar message="文件只能是.jar格式的文件"/>);
        }else {
            classEditHtml.map(function (item) {
                classNames += item.props.children[0]+':'+item.key+';';
            });
            let body = 'classNames';
            data[body] = classNames;
            let p = 'pluginName';
            data[p] = pluginName;
            return data;
        }
    }
    afterEditPluginSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.resetNewFile();
                this.setState({ editPluginVisible : false , classEditHtml : [] });
                this.getFetchPluginList();
            }
        }
    }
    closeEditPlugin(){
        this.resetNewFile();
        this.resetEditPlugin();
        this.setState({ editPluginVisible : false });
    }

    //添加数据源
    addDataSourceModal(){
        this.getFetchDataType();
        this.getFetchDataSourceType();
        this.setState({ addDataSourceVisible : true });
    }
    closeAddDataSource(){
        this.resetAddDataSource();
        this.setState({ addDataSourceVisible : false });
    }
    formAddDataSourceTirgger(trigger){
        this.resetAddDataSource = trigger.reset;
    }
    beforeAddDataSourceSubmit(data){
        let RegUrl = new RegExp();
        RegUrl.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        let name = data.name.replace(/(^\s*)|(\s*$)/g, "");
        let ip = data.ip.replace(/(^\s*)|(\s*$)/g, "");
        let port = data.port.replace(/(^\s*)|(\s*$)/g, "");
        let username = data.username.replace(/(^\s*)|(\s*$)/g, "");
        let password = data.password.replace(/(^\s*)|(\s*$)/g, "");
        let path = data.path.replace(/(^\s*)|(\s*$)/g, "");
        if(!name){
            popup(<Snackbar message="数据源名称不能为空"/>);
            return false;
        }else if(!ip){
            popup(<Snackbar message="数据源IP不能为空"/>);
            return false;
        }else if (!RegUrl.test(data.ip)){
            popup(<Snackbar message="请输入正确的IP"/>);
            return false;
        }else if(!port){
            popup(<Snackbar message="访问端口不能为空"/>);
            return false;
        }else if(!(/^\d*$/g.test(data.port))){
            popup(<Snackbar message="端口号必须为数字"/>);
            return false;
        }else if(!username){
            popup(<Snackbar message="用户名不能为空"/>);
            return false;
        }else if(!password){
            popup(<Snackbar message="密码不能为空"/>);
            return false;
        }else if(!path){
            popup(<Snackbar message="数据源路径不能为空"/>);
            return false;
        }else if(!data.dataSourceType){
            popup(<Snackbar message="数据源类型不能为空"/>);
            return false;
        }else if(!data.dataType){
            popup(<Snackbar message="数据类型不能为空"/>);
            return false;
        }else {
            let n = 'name';
            data[n] = name;
            let i = 'ip';
            data[i] = ip;
            let p = 'port';
            data[p] = port;
            let u = 'username';
            data[u] = username;
            let pwd = 'password';
            data[pwd] = password;
            let pat = 'path';
            data[pat] = path;
            return true;
        }
    }
    afterAddDataSourceSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.setState({ addDataSourceVisible : false });
                this.resetAddDataSource();
                this.getFetchDataSourceList();
            }
        }
    }

    //编辑数据源
    editDataSourceModal(dataSId){
        this.getFetchDataType();
        this.getFetchDataSourceType();
        let {dataSourceTable} = this.state;
        let dsId,dsName,dsIp,dsPort,dsPath,dsDataSourceTypeId,dsDataTypeId,dsUserName,dsPassWord;
        dataSourceTable.map(function (item) {
            if(item.dsId == dataSId){
                dsId = item.dsId;
                dsName = item.dsName;
                dsIp = item.dsIp;
                dsPort = item.dsPort;
                dsPath = item.dsPath;
                dsUserName = item.dsUserName;
                dsPassWord = item.dsPassWord;
                dsDataSourceTypeId = item.dsDataSourceTypeId;
                dsDataTypeId = item.dsDataTypeId;
            }
        });
        this.resetEditDataSource();
        this.setState({ dsId,dsName,dsIp,dsPort,dsPath,dsDataSourceTypeId,dsDataTypeId,dsUserName,dsPassWord,
            editDataSourceVisible : true });
    }
    beforeEditDataSourceSubmit(data){
        let RegUrl = new RegExp();
        RegUrl.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        let name = data.name.replace(/(^\s*)|(\s*$)/g, "");
        let ip = data.ip.replace(/(^\s*)|(\s*$)/g, "");
        let port = data.port.replace(/(^\s*)|(\s*$)/g, "");
        let username = data.username.replace(/(^\s*)|(\s*$)/g, "");
        let password = data.password.replace(/(^\s*)|(\s*$)/g, "");
        let path = data.path.replace(/(^\s*)|(\s*$)/g, "");
        if(!name){
            popup(<Snackbar message="数据源名称不能为空"/>);
            return false;
        }else if(!ip){
            popup(<Snackbar message="数据源IP不能为空"/>);
            return false;
        }else if (!RegUrl.test(data.ip)){
            popup(<Snackbar message="请输入正确的IP"/>);
            return false;
        }else if(!port){
            popup(<Snackbar message="访问端口不能为空"/>);
            return false;
        }else if(!(/^\d*$/g.test(data.port))){
            popup(<Snackbar message="端口号必须为数字"/>);
            return false;
        }else if(!username){
            popup(<Snackbar message="用户名不能为空"/>);
            return false;
        }else if(!password){
            popup(<Snackbar message="密码不能为空"/>);
            return false;
        }else if(!path){
            popup(<Snackbar message="数据源路径不能为空"/>);
            return false;
        }else if(!data.dataSourceType){
            popup(<Snackbar message="数据源类型不能为空"/>);
            return false;
        }else if(!data.dataType){
            popup(<Snackbar message="数据类型不能为空"/>);
            return false;
        }else {
            let n = 'name';
            data[n] = name;
            let i = 'ip';
            data[i] = ip;
            let p = 'port';
            data[p] = port;
            let u = 'username';
            data[u] = username;
            let pwd = 'password';
            data[pwd] = password;
            let pat = 'path';
            data[pat] = path;
            return data;
        }
    }
    afterEditDataSourceSubmit(data){
        if(data.code){
            popup(<Snackbar message={data.message} />);
        }else {
            popup(<Snackbar message={data.msg} />);
            if(data.status == 200){
                this.getFetchDataSourceList();
                this.setState({ editDataSourceVisible : false });
            }
        }
    }
    closeEditDataSource(){
        this.setState({ editDataSourceVisible : false });
    }
    formEditDataSourceTirgger(trigger){
        this.resetEditDataSource = trigger.reset;
    }

    //点击任务时切换
    handleTaskClick(index){
        let content = document.getElementsByClassName('dicManage_content_col');
        for(let i=0;i<content.length;i++){
            content[i].style.backgroundColor = null;
            content[i].style.border = null;
        }
        content[index].style.backgroundColor = '#ceeefc';
        content[index].style.border = "solid 1px #6fccf5";
        this.getFetchTaskList();
        this.setState({tag : 'task'});
    }
    //任务统计信息
    /*taskStatistics(){
        let statHtml = [];
        statHtml.push(
            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} key="">
                <div style={{display:'inline-block'}} className="dicManage_content_col">
                    <img src="image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                    <span className="statistic" style={{color:'#da8b5c'}}>
                        正在处理<br/>(5)
                    </span>
                </div>
                <div style={{display:'inline-block'}} className="dicManage_content_col">
                    <img src="image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                    <span className="statistic" style={{color:'#da8b5c'}}>
                        已完成<br/>(5)
                    </span>
                </div>
                <div style={{display:'inline-block'}} className="dicManage_content_col">
                    <img src="image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                    <span className="statistic" style={{color:'#da8b5c'}}>
                        处理失败<br/>(5)
                    </span>
                </div>
            </Col>
        );

    }*/
    //点击数据源时切换
    handleDataClick(index){
        let content = document.getElementsByClassName('dicManage_content_col');
        for(let i=0;i<content.length;i++){
            content[i].style.backgroundColor = null;
            content[i].style.border = null;
        }
        content[index].style.backgroundColor = '#ceeefc';
        content[index].style.border = "solid 1px #6fccf5";
        this.getFetchDataSourceList();
        this.setState({tag : 'dataSource'});
    }
    //点击plugin时切换
    handlePluginClick(index){
        let content = document.getElementsByClassName('dicManage_content_col');
        for(let i=0;i<content.length;i++){
            content[i].style.backgroundColor = null;
            content[i].style.border = null;
        }
        content[index].style.backgroundColor = '#ceeefc';
        content[index].style.border = "solid 1px #6fccf5";
        this.getFetchPluginList();
        this.setState({tag : 'plugin'});
    }

    //执行类型选项（添加任务模态框）
    taskExecuteType(){
        let {taskExecuteTypeList} = this.state;
        let executeHtml = [];
        executeHtml.push(
            <RadioGroup name="executeType" value={taskExecuteTypeList[0].value} type="inline" key=""
                        onChange={this.timing.bind(this)}>
                {
                    taskExecuteTypeList.map( ( item, index ) => {
                        return (
                            <Radio value={ item.value } key={index}>{ item.text }</Radio>
                        )
                    } )
                }
            </RadioGroup>
        );
        this.setState({executeHtml});
    }
    //执行类型为定时和周期时显示执行时间
    timing(value){
        let {taskExecuteTypeList} = this.state;
        let flag;
        taskExecuteTypeList.map(function (item) {
            if(item.value == value){
                if(item.text == '实时'){
                    flag = 1;
                }else {
                    flag = 0;
                }
            }
        });
        if(flag){
            this.setState({ showExeTime : false});
        }else {
            this.setState({ showExeTime : true});
        }
    }

    //任务类型为自定义时显示plugin
    customize(value){
        if(value == 'false'){
            this.setState({ showPlugin : true });
        }else {
            this.setState({ showPlugin : false });
        }
    }
    //编辑任务类型时控制plugin的显示
    customizeEdit(value){
        if(value == 'false'){
            this.setState({ showEditPlugin : true });
        }else {
            this.setState({ showEditPlugin : false });
        }
    }

    //执行任务
    executeTask(taskId){
        fetch(Uniplatform.context.url + '/nlap/admin/task/execute?taskId='+taskId,{
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res)=>res.json())
            .then((data)=>{
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else {
                    popup(<Snackbar message={data.msg}/>);
                    if (data.status == 200){
                        this.getFetchTaskList();
                    }
                }
            }).catch((err)=>console.log(err.toString()));
    }


    render() {
        let {index,total,size,index_data,size_data,total_data,index_plugin,size_plugin,total_plugin,
            dataSourceTable,pluginTable,taskTable,progressBool,
            selectedTask,selectedData,selectedDataStatus,selectedPlugin,selectedPluginStatus,
            selectedTaskStatus,addDataSourceVisible,editDataSourceVisible,tag,classEditHtml,
            dataSourceTypeList,dataTypeList,pluginTypeList,addPluginVisible,editPluginVisible,
            addTaskVisible,editTaskVisible,dataSourceList,businessList,pluginList,dataProcessTypeList,
            dsId,dsName,dsIp,dsPort,dsPath,dsUserName,dsPassWord,dsDataSourceTypeId,dsDataTypeId,
            executeHtml,showPlugin,showEditPlugin,showExeTime,classHtml,pluginId,pluginName,fileName,taskId,taskName,taskType,
            dataSourceId,businessId,pluginId_task,processedDataOperationId,executeType,executeTime} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <NLPMenu url={'/nlap/platform/task'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">任务管理</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                    <a onClick={this.handleTaskClick.bind(this,0)}>
                                        <div style={{display:'inline-block',width:'30.3%',backgroundColor:'#ceeefc',border:'solid 1px #6fccf5'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon1.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#da8b5c'}}>
                                                任务<br/>
                                                {/*<br/><a onClick={this.taskStatistics.bind(this)}>→</a>*/}
                                            </span>
                                        </div>
                                    </a>
                                    <a onClick={this.handleDataClick.bind(this,1)}>
                                        <div style={{display:'inline-block',width:'30.3%'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon2.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#84c7e5'}}>
                                                源数据源
                                            </span>
                                        </div>
                                    </a>
                                    <a onClick={this.handlePluginClick.bind(this,2)}>
                                        <div style={{display:'inline-block',width:'30.3%'}} className="dicManage_content_col">
                                            <img src="../image/dic_icon3.png" style={{margin:'7%',display:'inline-block'}}/>
                                            <span className="statistic" style={{color:'#98d37b'}}>
                                                PLUGIN
                                            </span>
                                        </div>
                                    </a>
                                </Col>
                            </div>
                        </Row>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className = "dicManage_span">{tag == 'task' ? '任务' : (tag == 'plugin' ? 'plugin' : '数据源')}</span>
                                </div>
                            </Col>
                            {tag == 'task' ?
                                <div className="dicManage_content">
                                    <ButtonGroup style={{float:'right',margin:'15px'}}>
                                        <Button onClick={this.addTaskModal.bind(this)}><i className="fa fa-plus"></i> 新建任务</Button>
                                        {total == 0 ?
                                            <Button disabled><i className="fa fa-trash"></i> 批量删除</Button>
                                            :
                                            <Button onClick={this.deleteTask.bind(this,selectedTask,selectedTaskStatus)}><i className="fa fa-trash"></i> 批量删除</Button>
                                        }
                                    </ButtonGroup>

                                    {total == 0 ?
                                        <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                            暂无数据
                                        </div>
                                        :
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                            <Table striped={true} checkable headBolder={ true } complex
                                                    onCheck={ this.handleTaskSelect } dataSource={taskTable}>
                                                <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                    { ( value, index ) => { return Number(`${Number(this.state.index-1)*Number(this.state.size)+Number(index)}`) + 1; } }
                                                </Column>
                                                <Column dataIndex="taskName" title="任务名称" scaleWidth='10%' textAlign="center"/>
                                                <Column title="任务类型" scaleWidth='10%' textAlign="center">
                                                    {(value)=>{
                                                        let type;
                                                        switch (value.taskType) {
                                                            case true:
                                                                type = '预定义';
                                                                break;
                                                            default:
                                                                type = '自定义';
                                                                break;
                                                        }
                                                        return type;
                                                    }}
                                                </Column>
                                                <Column dataIndex="dataSource" title="数据源" scaleWidth='8%' textAlign="center" />
                                                <Column dataIndex="business" title="业务策略" scaleWidth='10%' textAlign="center" />
                                                <Column dataIndex="plugin" title="plugin" scaleWidth='10%' textAlign="center" />
                                                <Column dataIndex="executeType" title="执行类型" scaleWidth='10%' textAlign="center" />
                                                <Column title="执行时间" scaleWidth='10%' textAlign="center" >
                                                    {(value)=>{
                                                        return value.executeTime ? value.executeTime : '';
                                                    }}
                                                </Column>
                                                <Column title="状态" scaleWidth='7%' textAlign="center" >
                                                    {(value)=>{
                                                        let type;
                                                        switch (value.taskStatus) {
                                                            case 0:
                                                                type = '已停止';
                                                                break;
                                                            default:
                                                                type = '运行中';
                                                                break;
                                                        }
                                                        return type;
                                                    }}
                                                </Column>
                                                {/*<Column title="更新时间" scaleWidth='10%' textAlign="center" >
                                                 {(value)=>{
                                                 return this.switchTime(value.updateTime);
                                                 }}
                                                 </Column>*/}
                                                <Column title="操作" scaleWidth='20%' textAlign="center" >
                                                    {( value )=>{
                                                        return(
                                                            <div style={{padding:'4px'}}>
                                                                {value.taskStatus ?
                                                                    <Button size="tiny" disabled>编辑</Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.editTaskModal.bind(this,value.taskId)}>
                                                                        编辑
                                                                    </Button>
                                                                }
                                                                {value.taskStatus ?
                                                                    <Button size="tiny" disabled>删除</Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.delTask.bind(this,value.taskId)}>
                                                                        删除
                                                                    </Button>
                                                                }
                                                                {value.taskStatus ?
                                                                    <Button size="tiny" onClick={this.executeTask.bind(this,value.taskId)}>
                                                                        停止
                                                                    </Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.executeTask.bind(this,value.taskId)}>
                                                                        运行
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
                                : ''
                            }
                            {tag == 'plugin' ?
                                <div className="dicManage_content">
                                    <ButtonGroup style={{float:'right',margin:'15px'}}>
                                        <Button onClick={this.addPluginModal.bind(this)}><i className="fa fa-plus"></i> 注册plugin</Button>
                                        {total_plugin == 0 ?
                                            <Button disabled><i className="fa fa-trash"></i> 批量删除</Button>
                                            :
                                            <Button onClick={this.deletePlugin.bind(this,selectedPlugin,selectedPluginStatus)}><i className="fa fa-trash"></i> 批量删除</Button>
                                        }
                                    </ButtonGroup>

                                    {total_plugin == 0 ?
                                        <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                            暂无数据
                                        </div>
                                        :
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                            <Table  striped={true} checkable headBolder={ true } complex
                                                    onCheck={ this.handlePluginSelect } dataSource={pluginTable}>
                                                <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                    { ( value, index ) => { return Number(`${Number(this.state.index_plugin-1)*Number(this.state.size_plugin)+Number(index)}`) + 1; } }
                                                </Column>
                                                <Column dataIndex="pluginName" title="plugin名称" scaleWidth='20%' textAlign="center"/>
                                                <Column dataIndex="fileName" title="文件名" scaleWidth='15%' textAlign="center"/>
                                                <Column title="状态" scaleWidth='15%' textAlign="center">
                                                    {(value)=>{
                                                        let status = [];
                                                        switch (value.pluginStatus){
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
                                                                                {value.tasks.map(function (task,i) {
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
                                                <Column title="创建时间" scaleWidth='15%' textAlign="center" >
                                                    {(value)=>{
                                                        return this.switchTime(value.uploadTime);
                                                    }}
                                                </Column>
                                                <Column title="修改时间" scaleWidth='15%' textAlign="center" >
                                                    {(value)=>{
                                                        return this.switchTime(value.updateTime);
                                                    }}
                                                </Column>
                                                <Column title="操作" scaleWidth='15%' textAlign="center" >
                                                    {( value )=>{
                                                        return(
                                                            <div style={{padding:'4px'}}>
                                                                {value.pluginStatus ?
                                                                    <Button size="tiny" disabled>编辑</Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.editPlugin.bind(this,value.pluginId)}>
                                                                        编辑
                                                                    </Button>
                                                                }
                                                                {value.pluginStatus ?
                                                                    <Button size="tiny" disabled>删除</Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.delPlugin.bind(this,value.pluginId)}>
                                                                        删除
                                                                    </Button>
                                                                }
                                                            </div>
                                                        )
                                                    }}
                                                </Column>
                                            </Table>
                                            <Divider/>
                                            <Pagination index={index_plugin} total={total_plugin} size={size_plugin} onChange={this.exChangePluginPagi.bind(this)}
                                                        align='center' showDataSizePicker={true} dataSizePickerList={['10','20','50','100']}/>
                                            <Divider/>
                                        </Col>
                                    }
                                </div>
                                : ''
                            }
                            {tag == 'dataSource' ?
                                <div className="dicManage_content">
                                    <ButtonGroup style={{float:'right',margin:'15px'}}>
                                        <Button onClick={this.addDataSourceModal.bind(this)}><i className="fa fa-plus"></i> 新建数据源</Button>
                                        {total_data == 0 ?
                                            <Button disabled><i className="fa fa-trash"></i> 批量删除</Button>
                                            :
                                            <Button onClick={this.deleteDataSource.bind(this,selectedData,selectedDataStatus)}><i className="fa fa-trash"></i> 批量删除</Button>
                                        }
                                    </ButtonGroup>

                                    {total_data == 0 ?
                                        <div style={{fontSize:'24px',color:'#7a8593',textAlign:'center',height:'200px',padding:'200px'}}>
                                            暂无数据
                                        </div>
                                        :
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                            <Table striped={true} checkable headBolder={ true } complex
                                                    onCheck={ this.handleDataSelect } dataSource={dataSourceTable}>
                                                <Column title="序号" scaleWidth = '5%' textAlign="center">
                                                    { ( value, index ) => { return Number(`${Number(this.state.index_data-1)*Number(this.state.size_data)+Number(index)}`) + 1; } }
                                                </Column>
                                                <Column dataIndex="dsName" title="数据源名称" scaleWidth='10%' textAlign="center"/>
                                                <Column dataIndex="dsIp" title="数据源IP" scaleWidth='10%' textAlign="center"/>
                                                <Column dataIndex="dsPort" title="访问端口" scaleWidth='8%' textAlign="center" />
                                                <Column dataIndex="dsPath" title="数据源URL" scaleWidth='10%' textAlign="center" />
                                                <Column dataIndex="dsDataSourceType" title="数据源类型" scaleWidth='10%' textAlign="center" />
                                                <Column dataIndex="dsDataType" title="数据类型" scaleWidth='9%' textAlign="center" />
                                                <Column title="状态" scaleWidth='8%' textAlign="center">
                                                    {(value)=>{
                                                        let status = [];
                                                        switch (value.dsStatus){
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
                                                                                {value.tasks.map(function (task,i) {
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
                                                <Column title="更新时间" scaleWidth='15%' textAlign="center" >
                                                    {(value)=>{
                                                        return this.switchTime(value.dsUpdateTime);
                                                    }}
                                                </Column>
                                                <Column title="操作" scaleWidth='15%' textAlign="center" >
                                                    {( value )=>{
                                                        return(
                                                            <div style={{padding:'4px'}}>
                                                                {value.dsStatus ?
                                                                    <Button size="tiny" disabled>编辑</Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.editDataSourceModal.bind(this,value.dsId)}>
                                                                        编辑
                                                                    </Button>
                                                                }
                                                                {value.dsStatus ?
                                                                    <Button size="tiny" disabled>删除</Button>
                                                                    :
                                                                    <Button size="tiny" onClick={this.deleteSource.bind(this,value.dsId)}>
                                                                        删除
                                                                    </Button>
                                                                }
                                                            </div>
                                                        )
                                                    }}
                                                </Column>
                                            </Table>
                                            <Divider/>
                                            <Pagination index={index_data} total={total_data} size={size_data} onChange={this.exChangeDataPagi.bind(this)}
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
                      action={contextUrl + '/nlap/admin/task/add'}
                      trigger={ this.formAddTaskTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={this.beforeAddTaskSubmit}
                      onAfterSubmit={this.afterAddTaskSubmit}
                >
                    <Modal visible={ addTaskVisible } size="medium" onClose={ this.closeAddTask.bind( this ) }>
                        <ModalHeader>
                            新建任务
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>任务名称</Label>
                                <Input name="taskName" placeholder="请输入任务名称" />
                            </FormItem>
                            <FormItem>
                                <Label>任务类型</Label>
                                <RadioGroup value="true" name="predefined" type="inline" onChange={this.customize.bind(this)}>
                                    <Radio value="true">预定义</Radio>
                                    <Radio value="false">自定义</Radio>
                                </RadioGroup>
                            </FormItem>
                            <FormItem>
                                <Label>数据源</Label>
                                <Select dataSource={dataSourceList} name="dataSourceId" placeholder="请选择数据源" showClear={false}/>
                            </FormItem>
                            {showPlugin ? '' :
                                <FormItem>
                                    <Label>业务策略</Label>
                                    <Select dataSource={businessList} name="businessId" placeholder="请选择业务策略" showClear={false}/>
                                </FormItem>
                            }
                            {showPlugin ?
                                <FormItem>
                                    <Label>plugin</Label>
                                    <Select dataSource={pluginList} name="pluginId" placeholder="请选择plugin" showClear={false}/>
                                </FormItem>
                                : ''}
                            <FormItem>
                                <Label>执行类型</Label>
                                {executeHtml}
                            </FormItem>
                            {showExeTime ?
                                <FormItem>
                                    <Label>执行时间</Label>
                                    <Input name="executeTime" placeholder="请输入执行时间" />
                                </FormItem>
                                : ''}
                            <FormItem>
                                <Label>数据处理方式</Label>
                                <Select dataSource={dataProcessTypeList} name="processedDataOperationId" placeholder="请选择数据处理方式" showClear={false}/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddTask.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">提交</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/task/edit'}
                      trigger={ this.formEditTaskTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={this.beforeEditTaskSubmit}
                      onAfterSubmit={this.afterEditTaskSubmit}
                >
                    <Modal visible={ editTaskVisible } size="medium" onClose={ this.closeEditTask.bind( this ) }>
                        <ModalHeader>
                            编辑任务
                        </ModalHeader>
                        <ModalBody>
                            <Input name="tID" style={{display:'none'}} value={taskId}/>
                            <FormItem>
                                <Label>任务名称</Label>
                                <Input name="taskName" placeholder="请输入数据源名称" value={taskName}/>
                            </FormItem>
                            <FormItem>
                                <Label>任务类型</Label>
                                <RadioGroup value={taskType ? 'true' : 'false'} name="predefined" type="inline" onChange={this.customizeEdit.bind(this)}>
                                    <Radio value="true">预定义</Radio>
                                    <Radio value="false">自定义</Radio>
                                </RadioGroup>
                            </FormItem>
                            <FormItem>
                                <Label>数据源</Label>
                                <Select dataSource={dataSourceList} value={dataSourceId} name="dataSourceId" placeholder="请选择数据源" showClear={false}/>
                            </FormItem>
                            {showEditPlugin ? '' :
                                <FormItem>
                                    <Label>业务策略</Label>
                                    <Select dataSource={businessList} value={businessId} name="businessId" placeholder="请选择业务策略" showClear={false}/>
                                </FormItem>
                            }
                            {showEditPlugin ?
                                <FormItem>
                                    <Label>plugin</Label>
                                    <Select dataSource={pluginList} value={pluginId_task} name="pluginId" placeholder="请选择plugin" showClear={false}/>
                                </FormItem>
                                : ''}
                            <FormItem disabled={true}>
                                <Label>执行类型</Label>
                                <Input value={executeType}/>
                            </FormItem>
                            {executeTime ?
                                <FormItem disabled={true}>
                                    <Label>执行时间</Label>
                                    <Input value={executeTime}/>
                                </FormItem>
                                : ''}
                            <FormItem disabled={true}>
                                <Label>数据处理方式</Label>
                                <Select dataSource={dataProcessTypeList} value={processedDataOperationId} showClear={false}/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeEditTask.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">提交</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/dataSource/add'}
                      trigger={ this.formAddDataSourceTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={this.beforeAddDataSourceSubmit}
                      onAfterSubmit={this.afterAddDataSourceSubmit}
                >
                    <Modal visible={ addDataSourceVisible } size="medium" onClose={ this.closeAddDataSource.bind( this ) }>
                        <ModalHeader>
                            新建数据源
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>数据源名称</Label>
                                <Input name="name" placeholder="请输入数据源名称" />
                            </FormItem>
                            <FormItem>
                                <Label>数据源IP</Label>
                                <Input name="ip" placeholder="例如:127.0.0.1" />
                            </FormItem>
                            <FormItem>
                                <Label>访问端口</Label>
                                <Input name="port" placeholder="请输入访问端口" />
                            </FormItem>
                            <FormItem>
                                <Label>用户名</Label>
                                <Input name="username" placeholder="请输入用户名" />
                            </FormItem>
                            <FormItem>
                                <Label>密码</Label>
                                <Input name="password" placeholder="请输入密码" />
                            </FormItem>
                            <FormItem>
                                <Label>数据源路径</Label>
                                <Input name="path" placeholder="请输入数据源路径" />
                            </FormItem>
                            <FormItem>
                                <Label>数据源类型</Label>
                                <Select dataSource={dataSourceTypeList} name="dataSourceType" placeholder="请选择数据源类型" showClear={false}/>
                            </FormItem>
                            <FormItem>
                                <Label>数据类型</Label>
                                <Select dataSource={dataTypeList} name="dataType" placeholder="请选择数据类型" showClear={false}/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddDataSource.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">提交</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form type="horizontal"
                      method="post"
                      action={contextUrl + '/nlap/admin/dataSource/edit'}
                      trigger={ this.formEditDataSourceTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={this.beforeEditDataSourceSubmit}
                      onAfterSubmit={this.afterEditDataSourceSubmit}
                >
                    <Modal visible={ editDataSourceVisible } size="medium" onClose={ this.closeEditDataSource.bind( this ) }>
                        <ModalHeader>
                            编辑数据源
                        </ModalHeader>
                        <ModalBody>
                            <Input name="id" style={{display:'none'}} value={dsId} />
                            <FormItem>
                                <Label>数据源名称</Label>
                                <Input name="name" placeholder="请输入数据源名称" value={dsName}/>
                            </FormItem>
                            <FormItem>
                                <Label>数据源IP</Label>
                                <Input name="ip" placeholder="请输入访问IP" value={dsIp}/>
                            </FormItem>
                            <FormItem>
                                <Label>访问端口</Label>
                                <Input name="port" placeholder="请输入访问端口" value={dsPort}/>
                            </FormItem>
                            <FormItem>
                                <Label>用户名</Label>
                                <Input name="username" placeholder="请输入用户名" value={dsUserName}/>
                            </FormItem>
                            <FormItem>
                                <Label>密码</Label>
                                <Input name="password" placeholder="请输入密码" value={dsPassWord}/>
                            </FormItem>
                            <FormItem>
                                <Label>数据源路径</Label>
                                <Input name="path" placeholder="请输入数据源路径" value={dsPath}/>
                            </FormItem>
                            <FormItem>
                                <Label>数据源类型</Label>
                                <Select dataSource={dataSourceTypeList} name="dataSourceType" value={dsDataSourceTypeId} placeholder="请选择数据源类型" showClear={false}/>
                            </FormItem>
                            <FormItem>
                                <Label>数据类型</Label>
                                <Select dataSource={dataTypeList} name="dataType" value={dsDataTypeId} placeholder="请选择数据类型" showClear={false}/>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeEditDataSource.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">保存</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Form method="post"
                      action={contextUrl + '/nlap/admin/plugin/add'}
                      trigger={ this.formAddPluginTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={ this.beforeAddPluginSubmit }
                      onAfterSubmit={this.afterAddPluginSubmit}
                >
                    <Modal visible={ addPluginVisible } size="medium" onClose={ this.closeAddPlugin.bind( this ) }>
                        <ModalHeader>
                            PLUGIN注册
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>plugin 名称</Label>
                                <Input name="pluginName" placeholder="请输入plugin名称" style={{display:'inline-block',width:'80%'}}/>
                            </FormItem>
                            <Row>
                                <Col size={{ normal: 10, small: 24, medium: 10, large: 10 }} style={{padding:'0'}}>
                                    <FormItem >
                                        <Label>className</Label>
                                        <Input placeholder="请输入类名" getter={this.formGetter} trigger={this.formTirgger} style={{display:'inline-block',width:'60%'}}/>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 10, small: 24, medium: 10, large: 10 }} style={{padding:'0'}}>
                                    <FormItem >
                                        <Label>plugin类型</Label>
                                        {/*<Select dataSource={pluginTypeList} getter={this.formPluginGetter}
                                                style={{display:'inline-block',width:'60%'}} />*/}
                                        <select id="selectPlugin" className="epm select dropdown-trigger" style={{width:'60%'}}>
                                            {pluginTypeList.map(function (item,index) {
                                                return(<option value={item.text} key={index}>{item.text}</option>)
                                            })}
                                        </select>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} style={{padding:'0'}}>
                                    <Button onClick={this.handleValue}>添加</Button>
                                </Col>
                            </Row>
                            <div style={{border:'1px dashed #d8dde6',borderRadius:'5px',width:'485px',height:'50px',marginLeft:'83px'}}>
                                {classHtml}
                            </div>
                            <Divider />
                            <FormItem>
                                <Label style={{width:'70px',textAlign:'right'}}>文件上传</Label>
                                <Upload limit={1} placeholder="请上传.jar格式的文件" name="file" style={{display:'inline-block',width:'80%'}} trigger={ this.formFileTirgger }/>
                            </FormItem>
                            { progressBool ? <Progress type="loading" /> : '' }
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddPlugin.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                      action={contextUrl + '/nlap/admin/plugin/edit'}
                      trigger={ this.formEditPluginTirgger }
                      async={true}
                      enctype="multipart/form-data"
                      onSubmit={ this.beforeEditPluginSubmit }
                      onAfterSubmit={this.afterEditPluginSubmit}
                >
                    <Modal visible={ editPluginVisible } size="medium" onClose={ this.closeEditPlugin.bind( this ) }>
                        <ModalHeader>
                            PLUGIN编辑
                        </ModalHeader>
                        <ModalBody>
                            <Input name="pId" style={{display:'none'}} value={pluginId}/>
                            <FormItem>
                                <Label>plugin 名称</Label>
                                <Input name="pluginName" value={pluginName} placeholder="请输入plugin名称" style={{display:'inline-block',width:'80%'}}/>
                            </FormItem>
                            <Row>
                                <Col size={{ normal: 10, small: 24, medium: 10, large: 10 }} style={{padding:'0'}}>
                                    <FormItem >
                                        <Label>className</Label>
                                        <Input placeholder="请输入类名" getter={this.formEditGetter} trigger={this.formEditTirgger} style={{display:'inline-block',width:'60%'}}/>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 10, small: 24, medium: 10, large: 10 }} style={{padding:'0'}}>
                                    <FormItem >
                                        <Label>plugin类型</Label>
                                        <select id="selectPlugin" className="epm select dropdown-trigger" style={{width:'60%'}}>
                                            {pluginTypeList.map(function (item,index) {
                                                return(<option value={item.text} key={index}>{item.text}</option>)
                                            })}
                                        </select>
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }} style={{padding:'0'}}>
                                    <Button onClick={this.handleEditValue}>添加</Button>
                                </Col>
                            </Row>
                            <div style={{border:'1px dashed #d8dde6',borderRadius:'5px',width:'485px',height:'50px',marginLeft:'83px'}}>
                                {classEditHtml}
                            </div>
                            <Divider />
                            <FormItem>
                                <Label style={{width:'70px',textAlign:'right'}}>文件上传</Label>
                                <Upload placeholder="上传新文件" name="file" style={{display:'inline-block',width:'80%'}} trigger={ this.formNewFileTirgger }/>
                                <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }} offset={6}>当前文件名：{fileName}</Col>
                            </FormItem>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeEditPlugin.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

                <Footer />
            </Page>
        );
    }

}
TaskManage.UIPage = page;
export default TaskManage;