import React, { Component } from 'react';
import {Page,Row,Col,ButtonGroup,Button,Icon,Table,Column,Text,Tabs,Tab,Card,CardBody,Pagination,
        Select,Modal,ModalHeader,ModalBody,ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,
        Divider,Accordion,Textarea,Alert} from 'epm-ui';
import { SingleBanner,util_bytesToSize,Timestamp,util_formatJson ,QueryAll,QueryField,QueryInput,
    Footer,COMM_HeadBanner,UsouMenu,NavigationBar } from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'Usou',
    css: [
        'css/searchdata.min.css',
        'css/indices.min.css',
        'css/singleBanner.min.css',
        'css/jsoneditor.min.css',
        'css/leftnav.min.css'
    ],
    js: [
        'js/json2.min.js',
        'js/jquery.jsoneditor.min.js'
    ]
};

const getDataEmp = (data) =>{
    return data ? data : 0;
};


class indicesDetail extends Component {
    constructor(props){
        super(props);
        this.state = {
            index: 1,
            size: 10,
            total:0,
            totalData:[],
            indiceList:[],
            health:[],
            stats:[],
            shards:[],
            aliases:[],
            aliaseTotal:[],
            visible: false,

            resultSearch:[],
            resultColumn:[],
            search:[],
            state:[],
            panelType : '',
            panelField :{},
            indicesType:[],
            keyNames:[],
            deleteAll:[],
            selectEd : '_all',
            body : '{"query":{"match_all":{}}}',
            updateVisible: false,
            source:[],
            sourceMessage:{},
            must: [],
            must_not :[],
            shold : [],
        }
        this.fetchIndicesDetail = this.fetchIndicesDetail.bind(this);
        this.fetchIndicesAliases = this.fetchIndicesAliases.bind(this);
        this.getClusterList = this.getClusterList.bind(this);
        this.beforeSubmit = this.beforeSubmit.bind(this);
        this.afterSubmit = this.afterSubmit.bind( this );
        this.exchangePage =  this.exchangePage.bind(this);
        this.aliasesPage =  this.aliasesPage.bind(this);
        this.formTirgger = this.formTirgger.bind( this );

        this.handleSelect = this.handleSelect.bind(this);
        this.getIndicesName = this.getIndicesName.bind(this);
        this.printJSON = this.printJSON.bind(this);
        this.jsonUpdateSubmit = this.jsonUpdateSubmit.bind(this);
    }

    componentDidMount() {
        this.fetchIndicesDetail();
    }

    fetchIndicesDetail(){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+this.props.page.indicesName+'/_detail' , {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let { health,stats } = data;
                this.setState( {health,stats } );
                this.getIndiceList();
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    fetchIndicesAliases(){
        let name = this.props.page.indicesName;
        let { index,size } = this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+name+'/_aliases' , {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let  aliases  = [];
                aliases.push(data);
                this.setState( { aliases } );
                this.getAliasesList();
                this.aliasesPage(index,size);
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    fetchShardsDetial(){
        let { index,size } = this.state;
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+this.props.page.indicesName+'/_shards' , {
            credentials: 'same-origin',
            method: 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let  shards  = this.state.shards;
                shards.push(data);
                this.setState( { shards } );
                this.exchangePage(index,size);
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    exchangePage(index,size){
        let shardsList = [];
        let totalData = this.state.shards[0];
        let total = totalData.length;
        this.setState({index,size,total});
        for(let i = (index-1) * size; i < index * size; i++){
            if(totalData[i]){
                shardsList.push(totalData[i])
            }
        }
        this.setState({shardsList});
    }

    aliasesPage(index,size){
        let aliaseList = [];
        let aliaseTotal = this.state.aliaseTotal;
        let total = aliaseTotal.length;
        this.setState({index,size,total});
        for(let i = (index-1) * size; i < index * size; i++){
            if(aliaseTotal[i]){
                aliaseList.push(aliaseTotal[i])
            }
        }
        this.setState({aliaseList});
    }

    getClusterList(){
        window.open('');
    }

    getIndiceList(){
        let indiceList = this.getCountIndice();
        this.setState({indiceList});
    }

    getCountIndice(){
        let {stats} = this.state;

            let indices = Object.keys(stats.indices).map(function (indicesName){

                let indices = {

                    indicesName :  indicesName,
                    docs :<span><p>主分片文档数量:</p><p>{getDataEmp(stats.indices[indicesName].primaries.docs.count)}</p></span>,
                    primaries_size:<span><p>主分片存储:</p><p>{util_bytesToSize(getDataEmp(stats._all.primaries.store.size_in_bytes))}</p></span>,
                    store_size: <span><p>总存储量:</p><p>{util_bytesToSize(getDataEmp(stats.indices[indicesName].primaries.store.size_in_bytes))}</p></span>,
                    shards:<span><p>总分片数:</p><p>{getDataEmp(stats._shards.total)}</p></span>,
                    max_docs: <span><p>最大文档数:</p><p>{getDataEmp(stats.indices[indicesName].primaries.docs.max)}</p></span>,
                    deleted_docs: <span><p>已删除文档:</p><p>{getDataEmp(stats.indices[indicesName].primaries.docs.deleted)}</p></span>,
                    /*搜索总计*/
                    query_total: <span><p>查询总数:</p><p>{getDataEmp(stats.indices[indicesName].primaries.search.query_total)}</p></span>,
                    query_time: <span><p>查询时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.search.query_time_in_millis))}</p></span>,
                    fetch_total: <span><p>提取总数:</p><p>{getDataEmp(stats.indices[indicesName].primaries.search.fetch_total)}</p></span>,
                    fetch_time: <span><p>提取时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.search.fetch_time_in_millis))}</p></span>,
                    /*索引总计*/
                    index_total: <span><p>索引总数:</p><p>{getDataEmp(stats.indices[indicesName].primaries.indexing.index_total)}</p></span>,
                    index_time: <span><p>索引时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.indexing.index_time_in_millis))}</p></span>,
                    delete_total: <span><p>删除总计:</p><p>{getDataEmp(stats.indices[indicesName].primaries.indexing.delete_total)}</p></span>,
                    delete_time: <span><p>删除时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.indexing.delete_time_in_millis))}</p></span>,
                    /*获取总计*/
                    total: <span><p>获取总计:</p><p>{getDataEmp(stats.indices[indicesName].primaries.get.total)}</p></span>,
                    time: <span><p>获取时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.get.time_in_millis))}</p></span>,
                    exists_total: <span><p>存在总数:</p><p>{getDataEmp(stats.indices[indicesName].primaries.get.exists_total)}</p></span>,
                    exists_time: <span><p>存在时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.get.exists_time_in_millis))}</p></span>,
                    missing_total: <span><p>缺少总数:</p><p>{getDataEmp(stats.indices[indicesName].primaries.get.missing_total)}</p></span>,
                    missing_time: <span><p>失踪时间:</p><p>{Timestamp(getDataEmp(stats.indices[indicesName].primaries.get.missing_time_in_millis))}</p></span>,
                }

                return indices;
            });
            return indices;

    }

    //别名
    getAliasesList(){
        let aliaseTotal = [];
        let aliases = this.state.aliases;
        Object.keys(aliases).map(function(alsIndex){
            Object.keys(aliases[alsIndex]).map(function(indiceIndex){
                Object.keys(aliases[alsIndex][indiceIndex].aliases).map(function(alsName){
                    let alsTable = {
                        aliaseName : alsName,
                        index_routing : aliases[alsIndex][indiceIndex].aliases[alsName].index_routing,
                        search_routing : aliases[alsIndex][indiceIndex].aliases[alsName].search_routing,
                        filter : aliases[alsIndex][indiceIndex].aliases[alsName].filter ? aliases[alsIndex][indiceIndex].aliases[alsName].filter : ''
                    }
                    aliaseTotal.push(alsTable);
                });
            });
        });
        this.setState({aliaseTotal});
    }

    getClusterColor(color){
        let result;
        switch(color){
            case 'green' : result = '#27c627';
                break;
            case 'yellow': result = '#ff9a3c';
                break;
            case 'red': result = '#c23934';
                break;
            default: result = '#aba4a4';
                break;
        }
        let colorDiv = <div style={{background:result,width:'26px',height:'16px',display:'inline-block'}}></div>;
        return colorDiv;
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
                window.opener.location.href = window.opener.location.href;
                window.close();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //删除索引
    deleteIndices(name){
        const confirmShow = confirm("确定要删除索引?");
        if(!confirmShow){
            return false;
        }
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
                window.opener.location.href = window.opener.location.href;
                window.close();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //清除缓存
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
    flushIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/_flush', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageFlush ='';
                if ( data.status == 200){
                    messageFlush = '操作成功！';
                } else {
                    messageFlush = '操作失败';
                }
                let { message } = data;
                this.setState( { mFlush:message,messageFlush:messageFlush } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //优化索引
    optimizeIndices(name){
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/'+ name +'/_optimize', {
            credentials: 'same-origin',
            method: 'POST'} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let messageOp ='';
                if ( data.status == 200){
                    messageOp = '操作成功！';
                } else {
                    messageOp = '操作失败';
                }
                let { message } = data;
                this.setState( { mOptimize:message,messageOp:messageOp } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    showAddAliase(){
        this.setState( { visible: true } );
    }
    handleClose() {
        this.setState( { visible: false } );
    }
    beforeSubmit(data){
        let bodys = '{"actions" : [{ "add" : { "index" : "'+this.props.page.indicesName+'", "alias" : "'+data.aliaseName+'","search_routing" : "'+data.search_routing+'", "index_routing" : "'+data.index_routing+'" } }]}';
        let body = "body";
        data[body] = bodys;
        return data;
    }
    afterSubmit(data){
        this.setState( { visible : false } );
        data.status == 200 ? popup(<Snackbar message="添加成功" />) : popup(<Alert message={data.message} type="error" dismissible />)
        this.fetchIndicesAliases();
        this.reset();
    }
    formTirgger( trigger ) {
        this.reset = trigger.reset;
    }
    //删除别名
    deleteAliase(aliaseName){
        let param = new FormData();
        param.append('body','{"actions" : [{ "remove" : { "index" : "'+this.props.page.indicesName+'","alias" : "'+aliaseName+'" } }]}');
        fetch( Uniplatform.context.url + '/usou/cluster/'+this.props.page.id+'/_aliases/remove', {
            credentials: 'same-origin',
            method: 'POST',
            body: param } )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                data.status == 200 ? popup(<Snackbar message="删除成功" />) : popup(<Alert message={'操作失败!   '+ data.message} type="error" dismissible />)
                this.fetchIndicesAliases();
            }).catch( ( err ) => console.log( err.toString() ) );
    }

    //数据查询
    fetchSearchData(){
        fetch( Uniplatform.context.url + '/usou/cluster/' + this.props.page.id + '/_search', {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let { search, state } = data;
                this.setState( { search, state } );
                this.getIndicesName();
                this.getIndicesTable("",{});
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    /*获取全部类型*/
    getIndicesName(){
        let state = this.state.state;
        let indicesType = [];
        let allType = [];
        if(state.hasOwnProperty("metadata")){
            let indices = Object.keys(state.metadata.indices)
            indices.map(() => {
                Object.keys(state.metadata.indices[this.props.page.indicesName].mappings).map((indexType) => {
                    allType.push(indexType);
                });
            });
            //去重
            [...new Set(allType)].map((index) => {
                let type = {
                    value :  index,
                    text : index
                };
                indicesType.push(type);
            });
        }
        this.setState( { indicesType } );
    }
    //获取table datasource
    getQueryDataSource() {
        let {search,resultSearch} = this.state;
        let keyNames = [];
        let metaColumns = ["_index", "_type", "_id", "_score"];
        keyNames = keyNames.concat(metaColumns);
        if(search.hasOwnProperty("hits")){
            resultSearch = Object.keys(search.hits.hits).map(function(index){
                let secdata = {
                    _index : search.hits.hits[index]._index,
                    _type : search.hits.hits[index]._type,
                    _id : search.hits.hits[index]._id,
                    _score : search.hits.hits[index]._score,
                }
                Object.assign(secdata,search.hits.hits[index]._source);
                Object.keys(search.hits.hits[index]._source).map((item) => {
                    keyNames.push( item );
                });
                return secdata;
            });
            keyNames = [...new Set(keyNames)]
        }
        this.setState({ resultSearch,keyNames });
    }
    getTableWidth(len){
        if(len >= 6){
            return '14%';
        }
    }
    //获取table column
    getQueryDataColumn(){
        let {keyNames} = this.state;
        const resultColumn = [];
        Object.keys(keyNames).map((keyIndex) => {
            let columnVal = {
                title:  keyNames[keyIndex],
                dataIndex: keyNames[keyIndex],
                scaleWidth : this.getTableWidth(Object.keys(keyNames).length),
                sub:(value,index,rowData) => {
                    let result = <div onClick={this.showUpdateData.bind(this,rowData)} style={{cursor: 'pointer'}}>{value}</div>;
                    return result;
                }
            }
            resultColumn.push(columnVal);
        });
        this.setState({ resultColumn });
    }
    deleteQuery(){
        let deleteAll = this.state.deleteAll;
        if(deleteAll == ""){
            popup( <Snackbar message={`没有选择要删除的数据`} /> );
            return false;
        }
        const confirmShow = confirm("确定要删除数据?");
        if(!confirmShow){
            return false;
        }
        let param = new FormData();
        param.append("docIds", deleteAll);
        fetch( Uniplatform.context.url + '/usou/cluster/'+ this.props.page.id + '/_muldelete', {
            credentials: 'same-origin',
            method: 'POST',
            body : param} )
            .then( ( res ) => res.json() )
            .then( ( data ) => {
                let {panelType,panelField} = this.state;
                if(data.status == '200'){
                    return setTimeout(() => {
                        this.getIndicesTable(panelType,panelField);
                        popup(<Snackbar message="删除成功" />);
                    }, 1000);
                }else{
                    return popup(<Alert message={`错误信息:${data.message}`} type="error" dismissible />);
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //删除
    handleSelect(data){
        let deleteAll = [];
        deleteAll = Object.keys(data).map((index) =>
            `${data[index]._index}/${data[index]._type}/${data[index]._id}`
        ).join(',');
        this.setState({deleteAll : deleteAll});
    }
    //数据查询
    getIndicesTable(panelType,panelField){
        let index = '';
        let panelIndice = this.props.page.indicesName;
        panelType = (panelType == "") ? "" : panelType;

        index = `${panelIndice}/${panelType}`;

        let body = "";
        if(Object.keys(panelField).length > 0){
            body = `{"query":{"bool":{"must":[{"wildcard":${JSON.stringify(panelField)}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"aggs":{},"version":true}`;
        }
        this.setState({panelType,panelField});
        let param = new FormData();
        param.append("index" , index);
        param.append("body", body);
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_search/common`, {
            credentials: 'same-origin',
            method : 'POST',
            body : param
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                if(data.status == "200"){
                    let resultSearch = [],resultColumn = [];
                    this.setState({resultSearch,resultColumn});
                    popup( <Alert message={`错误信息:${data.message}`} type="error" dismissible /> );
                }else{
                    let { search } = data;
                    this.setState( { search } );
                    this.getQueryDataSource();
                    this.getQueryDataColumn();
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //索引下类型点击事件
    addIndiceTypeClass(name){
        const {indicesType,panelField} = this.state;
        let panelType;
        Object.keys(indicesType).map(function (index) {
            if(indicesType[index].value == name){
                panelType = indicesType[index].value;
            }
        });
        this.setState({indicesType: indicesType});
        this.getIndicesTable(panelType,panelField);
    }
    //打印json
    printJSON() {
        let source = this.state.source;
        $('#json').val(JSON.stringify(source));
        this.setState({source});
    }
    //修改json
    updateJSON(option,value) {
        let source = value;
        this.setState({source});
        this.printJSON();
    }
    //监测json文本框变化
    textEditorJson(option,value){
        let val = value;
        let source = [];
        if (val) {
            try {
                source = JSON.parse(val);
                this.setState({ source });
            }
            catch (e) {
                popup( <Alert message={`错误信息:${err.message}`} type="error" dismissible /> );
            }
        } else {
            source = {};
        }
        $('#editor').jsonEditor(source, { change: this.updateJSON.bind(this,source) });
    }
    //打开模态框
    showUpdateData(rowData){
        fetch( `${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/${rowData._index}/${rowData._type}/${rowData._id}`, {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( valData ) => {
                if(valData.status == '200'){
                    let source = valData.data._source;
                    let sourceMessage = {
                        _index : rowData._index,
                        _type : rowData._type,
                        _id : rowData._id
                    };
                    $('#editor').jsonEditor(source, { change: this.updateJSON.bind(this,source) });
                    this.setState({source,sourceMessage});
                }
            }).catch( ( err ) => console.log( err.toString() ) );

        this.setState( { updateVisible: true } );
    }
    //模态框关闭
    handleUpdateClose(){
        this.setState( { updateVisible: false } );
    }
    //json修改提交
    jsonUpdateSubmit(data){
        let { panelType,panelField } = this.state;
        this.setState( { updateVisible : false } );
        if(data.status == '200'){
            return setTimeout(() => {
                this.getIndicesTable(panelType,panelField);
                popup(<Snackbar message="修改成功" />);
            }, 1000);
        }else{
            return popup(<Alert message={data.message} type="error" dismissible />);
        }
    }

    //导出
    getJsonDown(){
        let {panelType,panelField} = this.state;

        let body = '';
        let index = '';
        let panelIndice = this.props.page.indicesName;
        panelType = (panelType == "") ? "" : panelType;
        index = `${panelIndice}/${panelType}`
        if(Object.keys(panelField).length > 0){
            body = `{"query":{"bool":{"must":[{"wildcard":${JSON.stringify(panelField)}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"aggs":{},"version":true}`;
        }
        let param = new FormData();
        param.append("index",index);
        param.append("body",body);
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/downloadJson`, {
            credentials: 'same-origin',
            method : 'POST',
            body : param
        } )
            .then(response =>  response.blob())
            .then(blob => {
                let time = new Date();
                var url = window.URL.createObjectURL(blob);
                var a = document.createElement('a');
                a.href = url;
                a.download = `${time}.txt`;
                a.click();
            });
    }

    getJsonByIdDown(){
        let deleteAll = this.state.deleteAll;
        if(deleteAll == ""){
            popup( <Snackbar message={`没有选择要导出的数据`} /> );
            return false;
        }

        let param = new FormData();
        param.append("docIds", deleteAll);
        fetch( Uniplatform.context.url + '/usou/cluster/'+ this.props.page.id + '/downloadByDocId', {
            credentials: 'same-origin',
            method: 'POST',
            body : param} )
            .then(response =>  response.blob())
            .then(blob => {
                let time = new Date();
                var url = window.URL.createObjectURL(blob);
                var a = document.createElement('a');
                a.href = url;
                a.download = `${time}.txt`;
                a.click();
            });
    }

    render() {
        let {aliasName,indicesName,indicesField,  indiceList,health,stats,shardsList,index,size,total,aliaseList,visible,
            mClose,messageClose,mOpen,messageOpen,mDelete,messageDelete,mClean,messageClean,mRefresh,messageRe,mFlush,messageFlush,mOptimize,messageOp,
            indicesType,resultSearch,resultColumn,body,updateVisible,panelType,source,sourceMessage} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Divider/>
                <Row>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/indexManage'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                        <Row>
                            <NavigationBar code={'indicesDetail'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name} indices={this.props.page.indicesName}/>
                        </Row>
                        <Row>
                            <Button type="default" size="small" style={{float:'right',margin:'10px',position:'relative',zIndex:1}} >
                                <a style={{textDecoration:'none'}} href="javascript:location.reload();"><Icon icon="refresh" />&nbsp;&nbsp;刷新</a>
                            </Button>
                            <Text isBold={ true } style={{fontSize:'24px',textAlign:'center'}}>索引({this.props.page.indicesName})</Text>
                        </Row>
                        <Row className="indices_detail_row">
                            <Tabs>
                                <Tab title="概览">
                                    <div className="">
                                        <Table dataSource={ indiceList } hideHead className="table_style" striped={ true } multiLine={ true } bordered={ true } style={{backgroundColor:'#eef1f6',height:'120px'}}>
                                            <Column title="主分片文档数量" dataIndex="docs" textAlign="center" />
                                            <Column title="主分片存储" dataIndex="primaries_size" textAlign="center" />
                                            <Column title="总存储量" dataIndex="store_size" textAlign="center" />
                                            <Column title="总分片数" dataIndex="shards" textAlign="center" />
                                        </Table>
                                        <br/>
                                        <Text isBold={ true } style={{fontSize:'20px',textAlign:'center'}}><u>健康</u></Text>
                                        <div className="health_style" style={{textAlign:'center'}}>
                                            <div><pre style={{marginTop:'20px'}}>状态<br/>{ this.getClusterColor(health.status) }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>节点<br/>{ health.number_of_nodes }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>数据节点<br/>{ health.number_of_data_nodes }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>主分片<br/>{ health.active_primary_shards }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>活动分片<br/>{ health.active_shards }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>迁移分片<br/>{ health.relocating_shards }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>初始化分片<br/>{ health.initializing_shards }</pre></div>
                                            <div><pre style={{marginTop:'20px'}}>未分配分片<br/>{ health.unassigned_shards }</pre></div>
                                        </div>
                                        <Table dataSource={ indiceList } hideHead className="table_style" striped={ true } multiLine={ true } bordered={ true } style={{backgroundColor:'#eef1f6',height:'120px'}}>
                                            <Column title="主分片文档数量" dataIndex="docs" textAlign="center" />
                                            <Column title="最大文档数" dataIndex="max_docs" textAlign="center" />
                                            <Column title="已删除文档" dataIndex="deleted_docs" textAlign="center" />
                                            <Column title="主分片存储" dataIndex="primaries_size" textAlign="center" />
                                            <Column title="总存储量" dataIndex="store_size" textAlign="center" />
                                        </Table>
                                        <br/>
                                        <Text isBold={ true } style={{fontSize:'20px',textAlign:'center'}}>搜索总计</Text>
                                        <Table dataSource={ indiceList } hideHead className="table_style" striped={ true } multiLine={ true } bordered={ true } style={{backgroundColor:'#eef1f6',height:'120px'}}>
                                            <Column title="查询总数" dataIndex="query_total" textAlign="center" />
                                            <Column title="查询时间" dataIndex="query_time" textAlign="center" />
                                            <Column title="提取总数" dataIndex="fetch_total" textAlign="center" />
                                            <Column title="提取时间" dataIndex="fetch_time" textAlign="center" />
                                        </Table>
                                        <br/>
                                        <Text isBold={ true } style={{fontSize:'20px',textAlign:'center'}}>索引总计</Text>
                                        <Table dataSource={ indiceList } hideHead className="table_style" striped={ true } multiLine={ true } bordered={ true } style={{backgroundColor:'#eef1f6',height:'120px'}}>
                                            <Column title="索引总数" dataIndex="index_total" textAlign="center" />
                                            <Column title="索引时间" dataIndex="index_time" textAlign="center" />
                                            <Column title="删除总计" dataIndex="delete_total" textAlign="center" />
                                            <Column title="删除时间" dataIndex="delete_time" textAlign="center" />
                                        </Table>
                                        <br/>
                                        <Text isBold={ true } style={{fontSize:'20px',textAlign:'center'}}>获取总计</Text>
                                        <Table dataSource={ indiceList } hideHead className="table_style" striped={ true } multiLine={ true } bordered={ true } style={{backgroundColor:'#eef1f6',height:'120px'}}>
                                            <Column title="获取总计" dataIndex="total" textAlign="center" />
                                            <Column title="获取时间" dataIndex="time" textAlign="center" />
                                            <Column title="存在总数" dataIndex="exists_total" textAlign="center" />
                                            <Column title="存在时间" dataIndex="exists_time" textAlign="center" />
                                            <Column title="缺少总数" dataIndex="missing_total" textAlign="center" />
                                            <Column title="失踪时间" dataIndex="missing_time" textAlign="center" />
                                        </Table>


                                    </div>
                                </Tab>
                                <Tab title="分片详情" onClick={this.fetchShardsDetial.bind(this)}>
                                    <div >
                                        <Table dataSource={ shardsList } className="table_style" striped={ true } multiLine={ true } bordered={ true } >
                                            <Column dataIndex="shard" title="分片" textAlign="center" />
                                            <Column dataIndex="state" title="状态" textAlign="center" />
                                            <Column dataIndex="docs" title="文档" textAlign="center" />
                                            <Column dataIndex="store" title="占用空间" textAlign="center" />
                                            <Column title="分片角色" textAlign="center" >
                                                { ( value,prirep ) => {
                                                    let role;
                                                    switch(value.prirep){
                                                        case 'p': role = '主分片';
                                                            break;
                                                        case 'r': role = '副本';
                                                            break;
                                                    }
                                                    return role;
                                                } }
                                            </Column>
                                            <Column dataIndex="node" title="节点" textAlign="center" />
                                        </Table>
                                        <Pagination index={ index }  total={ total>1000?1000:total } size={ size } align='right' onChange={this.exchangePage}/>
                                    </div>
                                </Tab>
                                <Tab title="别名" onClick={this.fetchIndicesAliases.bind(this)}>
                                    <Button type="default" size="tiny" onClick={this.showAddAliase.bind(this)} style={{float:'left',margin:'10px',position:'relative',zIndex:1}} >
                                        <Icon icon="plus" />&nbsp;&nbsp;新增别名
                                    </Button>
                                    <div >
                                        <Table dataSource={ aliaseList } striped={ true } multiLine={ true } bordered={ true } >
                                            <Column title="别名" dataIndex="aliaseName" textAlign="center"/>
                                            <Column title="索引路由" dataIndex="index_routing" textAlign="center"/>
                                            <Column title="搜索路由" dataIndex="search_routing" textAlign="center"/>
                                            <Column title="过滤" dataIndex="filter" textAlign="center"/>
                                            <Column title="操作" textAlign="center">
                                                { ( value ) => {
                                                    return (
                                                        <div>
                                                            <Button type="danger" size="small" onClick={this.deleteAliase.bind(this,value.aliaseName)}>
                                                                <Icon icon="trash-o" /> 删除
                                                            </Button>
                                                        </div>
                                                    )
                                                } }
                                            </Column>
                                        </Table>
                                        <Pagination index={ index }  total={ total>1000?1000:total } size={ size } align='right' onChange={this.aliasesPage}/>
                                    </div>
                                </Tab>
                                <Tab title="管理">
                                    <Button type="default" size="tiny" onClick={this.flushIndices.bind(this,this.props.page.indicesName) } style={{marginRight:'25px'}} >flush</Button>
                                    <Button type="default" size="tiny" onClick={this.cleanIndices.bind(this,this.props.page.indicesName) } style={{marginRight:'25px'}} >清除缓存</Button>
                                    <Button type="default" size="tiny" onClick={this.optimizeIndices.bind(this,this.props.page.indicesName) } style={{marginRight:'25px'}} >优化索引</Button>
                                    <Button type="default" size="tiny" onClick={this.refreshIndices.bind(this,this.props.page.indicesName) } style={{marginRight:'25px'}} >刷新索引</Button>
                                    <Button type="default" size="tiny" onClick={this.closeIndices.bind(this,this.props.page.indicesName) } style={{marginRight:'25px'}} >关闭索引</Button>
                                    <Button type="default" size="tiny" onClick={this.deleteIndices.bind(this,this.props.page.indicesName) } style={{marginRight:'25px'}} >删除索引</Button>
                                    <br/><br/>
                                    <div>
                                        <Text>返回结果</Text>
                                        <Card>
                                            <CardBody>
                                                <p>{messageClose||messageOpen||messageDelete||messageClean||messageRe||messageFlush||messageOp}</p>
                                                <p>{mClose||mOpen||mDelete||mClean||mRefresh||mFlush||mOptimize}</p>
                                            </CardBody>
                                        </Card>
                                    </div>

                                </Tab>
                                <Tab title="数据查询" onClick={this.fetchSearchData.bind(this)}>
                                    <Row>
                                        <Col size={{ normal: 24, small: 24, medium: 6, large: 6 }} style={{paddingLeft:'0px'}}>
                                            <Label style={{display:'inline-block',width:'initial'}}>类型</Label>
                                            <Select style={{ display:'inline-block',width:'80%',textAlign:'left' }} name="indicesType" dataSource={ indicesType } placeholder="请选择类型" onChange={this.addIndiceTypeClass.bind(this)}/>
                                        </Col>
                                        <Col size={{ normal: 24, small: 24, medium: 18, large: 18 }}>
                                            <ButtonGroup style={{float:'right',position:'relative',zIndex:1}}>
                                                <Button type="default" onClick={this.deleteQuery.bind(this)}>删除</Button>
                                                <Button type="default" onClick={this.getJsonDown.bind(this) } >导出全部</Button>
                                                <Button type="default" onClick={this.getJsonByIdDown.bind(this) } >导出选中</Button>
                                            </ButtonGroup>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Divider/>
                                        {
                                            Object.keys(resultSearch).length > 0 ?
                                                <Table textAlign="center" dataSource={resultSearch} columns={resultColumn} striped={ true } bordered={ true } checkable complex onCheck={ this.handleSelect }/>
                                                :''
                                        }
                                    </Row>

                                </Tab>
                            </Tabs>
                            <div>
                                <br/><br/><br/><br/><br/><br/><br/><br/><br/>
                            </div>
                        </Row>
                    </Col>
                </Row>

                <Modal visible={ visible } >
                    <ModalBody>
                        <Form type="horizontal"
                              method="post"
                              action={ contextUrl + '/usou/cluster/'+this.props.page.id+'/_aliases/add'}
                              async={true}
                              onSubmit={ this.beforeSubmit }
                              onAfterSubmit={this.afterSubmit}
                              trigger={ this.formTirgger }
                        >
                            <FormItem required={true}>
                                <Label>别名</Label>
                                <Input name="aliaseName" placeholder="请输入别名" />
                            </FormItem>
                            <FormItem>
                                <Label>索引路由</Label>
                                <Input name="index_routing" placeholder="请输入索引路由" />
                            </FormItem>
                            <FormItem>
                                <Label>搜索路由</Label>
                                <Input name="search_routing" placeholder="请输入搜索路由" />
                            </FormItem>

                            <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </Form>
                    </ModalBody>
                </Modal>
                <Form type="horizontal"
                      method="post"
                      action={ `${contextUrl}/usou/cluster/${this.props.page.id}/${sourceMessage._index}/${sourceMessage._type}/${sourceMessage._id}/_update` }
                      async={true}
                      onAfterSubmit={this.jsonUpdateSubmit}
                >
                    <Modal visible={ updateVisible } size="large" onClose={ this.handleUpdateClose.bind( this ) }>
                        <ModalHeader>
                            记录编辑
                        </ModalHeader>
                        <ModalBody>
                            <Row>
                                <Col style={{ paddingLeft:'0',paddingRight:'0',display:'none'}} >
                                    <Textarea id="json" rows={30} value={JSON.stringify(source)} name="body"/>
                                </Col>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{ paddingLeft:'0',paddingRight:'0'}}>
                                    <div id="editor" className="json-editor"></div>
                                </Col>
                            </Row>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={ this.handleUpdateClose.bind( this ) }>关闭</Button>
                            <Button type="primary" htmlType="submit">保存</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Footer />
            </Page>
        );
    }

}

indicesDetail.UIPage = page;
export default indicesDetail;
