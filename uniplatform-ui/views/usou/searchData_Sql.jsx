import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Text,Pagination,context,Select,Option,
   Snackbar,popup,Icon,ButtonGroup,FormItem,Input,Form,Tabs,Tab,Textarea,Accordion,Alert,
   Checkbox,Label,Modal,ModalHeader,ModalBody,ModalFooter} from 'epm-ui';
import { SingleBanner,util_formatJson,QueryGroup,Footer,COMM_HeadBanner,UsouMenu,NavigationBar} from '../../components/uniplatform-ui';

const page = {
    title: 'Usou',
    css: [
        'css/searchdata.min.css',
        'css/singleBanner.min.css',
        'css/jsoneditor.min.css',
        'css/jquery.json-viewer.min.css',
        'css/leftnav.min.css'
    ],
    js: [
        'js/json2.min.js',
        'js/jquery.jsoneditor.min.js',
        'js/jquery.json-viewer.min.js'
    ]
};
const contextUrl = '/uniplatform';

class SearchData extends Component {
    constructor(props){
        super(props);
        this.state = {
            resultSearch:[],
            resultColumn:[],
            search:[],
            state:[],
            aliasName:[],
            panelIndice:[],
            panelType : '',
            panelField :[],
            indicesName:[],
            indicesType:[],
            indicesField:[],
            keyNames:[],
            deleteAll:[],
            selectEd : '_all',
            body : '{"query":{"match_all":{}}}',
            updateVisible: false,
            tabFlag:'query', //tab标识 query:数据浏览 basic:基本查询 complex:复合查询 sql:SQL查询
            basicForm : {},
            source:[],
            sourceMessage:{},
            resultBasic:[],
            must: [],
            must_not :[],
            shold : [],
            selectSource: ['match_all','_all'],
            sqlVal:'select * from index',
            sqlExplain:[],
            explainShow:false,
            sqlShow:false,
            sqlError:{},
            count:1,
            queryCount:[1],
            basicSize:'10',
            basicIndex : '',
            queryBool : [],
            basicSubFlag :false,
            resultsql:[],
            sqlIndex:'',
            basicSearchShow:'',
            took : '',
            total : '',
            sqldata:{}
        }
        this.handleSelect = this.handleSelect.bind(this);
        this.getIndicesName = this.getIndicesName.bind(this);
        this.fetchSearchIndice = this.fetchSearchIndice.bind(this);
        this.afterComplexSubmit = this.afterComplexSubmit.bind(this);
        this.printJSON = this.printJSON.bind(this);
        this.changeSource = this.changeSource.bind(this);
        this.jsonUpdateSubmit = this.jsonUpdateSubmit.bind(this);
        this.beforeComplexSubmit = this.beforeComplexSubmit.bind(this);
        this.getComplexData = this.getComplexData.bind(this);
        this.changeBasicSize = this.changeBasicSize.bind(this);
        this.beforeBasicUpdate = this.beforeBasicUpdate.bind(this);
        this.getQueryBool = this.getQueryBool.bind(this);
        this.afterBasicUpdate = this.afterBasicUpdate.bind(this);
        this.getFetchBasic = this.getFetchBasic.bind(this);
        this.changeSqlIndex = this.changeSqlIndex.bind(this);
    }
    componentDidMount() {
        this.fetchSearchData();
    }
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
               /*  this.getQueryDataSource(); */
                this.getQueryDataColumn();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    fetchSearchIndice(dataString){
        let selectEd  =  dataString;
        this.setState({ selectEd : selectEd });
        let indicesName = this.state.indicesName;
        Object.keys(indicesName).map( (item) => {
            indicesName[item].visibility = false;
        });
        this.setState({ indicesName });
        let param = new FormData();
        param.append("index", selectEd);
        param.append("body", "");
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_search/common`, {
            credentials: 'same-origin',
            method : 'POST',
            body : param
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                let { search } = data;
                this.setState( { search } );
                this.getQueryDataSource();
                this.getQueryDataColumn();
                this.getColor();
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    /*获取全部索引、类型、字段*/
    getIndicesName(){
        let state = this.state.state;
        let indicesName = [],indicesType = [],indicesField = [],aliasName=[];
        let allType = [],allField=[],
            firstAlias = {
                value : '_all',
                text : '所有索引',
            };
        if(state.hasOwnProperty("metadata")){
            let indices = Object.keys(state.metadata.indices)
            indices.map((indexName) => {
                if(state.metadata.indices[indexName].state == "open"){
                    let name = {
                        visibility : false,
                        value : indexName
                    };
                    indicesName.push(name);
                    Object.keys(state.metadata.indices[indexName].aliases).map((indiceIndex) => {
                        let text = state.metadata.indices[indexName].aliases[indiceIndex];
                        let aliaData = {
                            value : indexName,
                            text : text
                        }
                        let flag = true;
                        aliasName.map((aliasNameIndex) => {
                            if(aliasNameIndex.text == text){
                                aliasNameIndex.value = aliasNameIndex.value + "," + indexName;
                                flag = false;
                            }
                        });
                        if(flag){
                            aliasName.push(aliaData);
                        }
                    });
                    Object.keys(state.metadata.indices[indexName].mappings).map((indexType) => {
                        allType.push(indexType);
                        Object.keys(state.metadata.indices[indexName].mappings[indexType].properties).map((indexField) => {
                            allField.push(indexField);
                        });
                    });
                }
            });
            //去重
            [...new Set(allType)].map((index) => {
                let type = {
                    visibility : false,
                    value : index
                };
                indicesType.push(type);
            });
            [...new Set(allField)].map((index) => {
                let field = {
                    visibility : false,
                    value : index
                };
                indicesField.push(field);
            });
        }
        aliasName.unshift(firstAlias);
        this.setState( { aliasName,indicesName,indicesType,indicesField } );
    }
    //获取table datasource
    getQueryDataSource() {
        let {search,resultSearch} = this.state;
        let keyNames = [];
        let took = '';
        let total = '';
        let metaColumns = ["_index", "_type", "_id", "_score"];
        keyNames = keyNames.concat(metaColumns);
        //计算为秒
        took = (search.took % (1000 * 60)) / 1000;
        if(search.hasOwnProperty("hits")){
            total = search.hits.total;

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
        this.setState({ resultSearch,keyNames,took,total });
    }
    getTableWidth(len){
        if(len >= 10){
            return '15%';
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
                    let result = <div onClick={this.showUpdateData.bind(this,rowData)} style={{cursor: 'pointer',marginTop:'7px',overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap'}}>{value}</div>;
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
                let {tabFlag,panelIndice,panelType,panelField} = this.state;
                if(tabFlag == 'query'){
                    if(data.status == '200'){
                        return setTimeout(() => {
                            this.getIndicesTable(panelIndice,panelType,panelField);
                            popup(<Snackbar message="删除成功" />);
                        }, 1000);
                    }else{
                        return popup(<Snackbar message={`错误信息:${data.message}`} />);
                    }
                }else if(tabFlag == 'basic'){
                    if(data.status == '200'){
                        return setTimeout(() => {
                            this.getFetchBasic();
                            popup(<Snackbar message="删除成功" />);
                        }, 1000);
                    }else{
                        return popup(<Snackbar message={`错误信息:${data.message}`} />);
                    }
                }else if(tabFlag == 'complex'){
                    if(data.status == '200'){
                        return setTimeout(() => {
                            this.getComplexData();
                            popup(<Snackbar message="删除成功" />);
                        }, 1000);
                    }else{
                        return popup(<Snackbar message={`错误信息:${data.message}`} />);
                    }
                }else if(tabFlag == 'sql'){
                    if(data.status == '200'){
                        return setTimeout(() => {
                            this.searchSql();
                            popup(<Snackbar message="删除成功" />);
                        }, 1000);
                    }else{
                        return popup(<Snackbar message={`错误信息:${data.message}`} />);
                    }
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
    //下拉框选择后,索引相应变化
    getColor(){
        const { indicesName,selectEd, indicesType,indicesField} = this.state;
        if(selectEd != "_all"){
            if(selectEd.includes(",")){
                let selectAll = selectEd.split(",");
                Object.keys(indicesName).map( (item) => {
                    selectAll.map((index) => {
                        if(indicesName[item].value == index){
                            indicesName[item].visibility = true;
                        }
                    });
                });
            }else{
                Object.keys(indicesName).map( (item) => {
                    if(indicesName[item].value == selectEd){
                        indicesName[item].visibility = true;
                    }
                });
            }
        }
        indicesType.map((item, index) => {
            item.visibility = false;
        });
        indicesField.map((item, index) => {
            item.visibility = false;
        });
        let panelIndice = '',
            panelType = '',
            panelField = {}
        this.setState({ indicesName ,indicesType,indicesField,panelIndice,panelType,panelField});
    }
    getIndicesTable(panelIndice,panelType,panelField){
        let index = '';
        
        if(panelIndice.length == 0){
            panelIndice = "_all";
        }else{
            panelIndice.map((item,indicIndex) =>
                item
            ).join(",");
        }

        panelType = (panelType == "") ? "" : panelType;

        index = `${panelIndice}/${panelType}`;

        let body = "";

        if(panelField.length > 0){
            let wildcard = [];
            panelField.map( (item,selIndex) => {
                let wild = {};
                wild["wildcard"] = item.fieldName;
                wildcard.push(wild);
            });

            body = `{"query":{"bool":{"must":${JSON.stringify(wildcard)},"must_not":[],"should":[]}},"from":0,"size":20,"sort":[],"aggs":{},"version":true}`;
        } else {
             body = `{"query":{"bool":{"must":[],"must_not":[],"should":[]}},"from":0,"size":20,"sort":[],"aggs":{},"version":true}`;
        }

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
                    popup( <Snackbar message={`错误信息:${data.message}`} /> );
                }else{
                    let { search } = data;
                    this.setState( { search } );
                    this.getQueryDataSource();
                    this.getQueryDataColumn();
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //索引点击事件
    addIndiceNameClass(index){
        let { indicesName,panelType,panelField,panelIndice,state,indicesType } = this.state;

        if(indicesName[index].visibility){
            indicesName[index].visibility = false;
            panelIndice.splice(panelIndice.indexOf(indicesName[index].value),1)
        }else{
            indicesName[index].visibility = true;
            panelIndice.push(indicesName[index].value);
        }

        if(panelType != ''){
            panelIndice.map((item,indicIndex) =>{
                if(state.hasOwnProperty("metadata")){
                    if(Object.keys(state.metadata.indices[item].mappings).indexOf(panelType) == -1){
                        indicesType.map((typeitem, typeindex) => {
                            if(typeitem.value == panelType){
                                typeitem.visibility = false;
                            }
                        });
                        panelType = '';
                    }
                }
            });
        }

		this.setState({indicesName ,panelIndice,panelType,indicesType});
        this.getIndicesTable(panelIndice,panelType,panelField);
    }
    //索引下类型点击事件
    addIndiceTypeClass(index){
        const {panelIndice, indicesType,panelField,state,indicesName } = this.state;
        let panelType = '';
        if(indicesType[index].visibility){
            indicesType[index].visibility = false;
            panelType = "";
        }else{
            indicesType.map((item, index) => {
                item.visibility = false;
            });
            indicesType[index].visibility = true;
            panelType = indicesType[index].value;
        }

        if(panelIndice.length > 0){

            panelIndice.map((item,indicIndex) =>{
                if(state.hasOwnProperty("metadata")){
                    if(Object.keys(state.metadata.indices[item].mappings).indexOf(panelType) == -1){
                        panelIndice.splice(panelIndice.indexOf(item),1);
                        indicesName.map((indixItem,indicIndex) => {
                             if(indixItem.value == item){
                                 indixItem.visibility = false;
                             }
                        })
                    }
                }
            });
        }

		this.setState({indicesType,panelType,indicesName,panelIndice});
        this.getIndicesTable(panelIndice,panelType,panelField);
    }
    //索引下字段点击事件
    addIndiceFieldClass(index){
        const { indicesField } = this.state;
        indicesField[index].visibility = indicesField[index].visibility ? false : true ;
		this.setState({indicesField: indicesField});
    }
    //监控索引下字段值
    changeIndeciField(index,opion,data){
        const { indicesField, panelIndice ,panelType,panelField } = this.state;
        if(data != ""){
            panelField.map( (item,selIndex) => {
                if(index == item.key){
                    panelField.splice(selIndex,1);
                }
            });

            let fields = {},fieldData = {};
            fieldData[indicesField[index].value] = `${data}*`;
            fields["key"] = index;
            fields["fieldName"] = fieldData;

            panelField.push(fields);

        }else{
            panelField.map( (item,selIndex) => {
                if(index == item.key){
                    panelField.splice(selIndex,1);
                }
            });
        }
        this.setState({ panelField });
        this.getIndicesTable(panelIndice,panelType,panelField);
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
     //打开模态框
    showUpdateData(rowData){
        if(!(rowData._index==undefined||rowData._index.length==0||rowData._type==undefined||rowData._type.length==0||rowData._id==undefined||rowData._id.length==0)){
            fetch( `${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/${rowData._index}/${rowData._type}/${rowData._id}`, {
            credentials: 'same-origin',
            method : 'GET'
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
    }
    //模态框关闭
    handleUpdateClose(){
        this.setState( { updateVisible: false } );
    }
    //json修改提交
    jsonUpdateSubmit(data){
        let { panelIndice,panelType,panelField,tabFlag } = this.state;
        this.setState( { updateVisible : false } );
        if(tabFlag == 'query'){
            if(data.status == '200'){
                return setTimeout(() => {
                    this.getIndicesTable(panelIndice,panelType,panelField);
                    popup(<Snackbar message="修改成功" />);
                }, 1000);
            }else{
                return popup(<Snackbar message={data.message} />);
            }
        }else if(tabFlag == 'basic'){
            if(data.status == '200'){
                return setTimeout(() => {
                    this.getFetchBasic();
                    popup(<Snackbar message="修改成功" />);
                }, 1000);
            }else{
                return popup(<Snackbar message={data.message} />);
            }
        }else if(tabFlag == 'complex'){
            if(data.status == '200'){
                return setTimeout(() => {
                    this.getComplexData();
                    popup(<Snackbar message="修改成功" />);
                }, 1000);
            }else{
                return popup(<Snackbar message={data.message} />);
            }
        }else if(tabFlag == 'sql'){
            if(data.status == '200'){
                return setTimeout(() => {
                    this.searchSql();
                    popup(<Snackbar message="修改成功" />);
                }, 1000);
            }else{
                return popup(<Snackbar message={data.message} />);
            }
        }
    }
    //监测textarea变化
    textChange(option,data){
        let body = data;
        return body != "" ? this.setState({ body : body }) : this.setState({ body : '{ }' });
    }
    //验证JSON
    stringToJson(){
        let body = this.state.body;
        try {
            JSON.parse(body)
            return true
        } catch (err) {
            popup( <Snackbar message={`错误信息:${err.message}`} /> );
        }
    }
    //易读JSON格式化
    getPretty(){
        let body = this.state.body;
        let newText = util_formatJson(body);
        this.setState({body : newText});
    }
    //存储复合查询表单值
    beforeComplexSubmit(data){
        let basicForm = data;
        this.setState({ basicForm });
        return data;
    }
    //接收复合查询返回值
    afterComplexSubmit(data){
        if(data.status == '400'){
            popup( <Snackbar message={`错误信息:${data.message}`} /> );
        }else{
            let { search } = data;
            this.setState( { search} );
            this.getQueryDataSource();
            this.getQueryDataColumn();
        }
    }
    //复合查询链接
    getComplexData(){
        let basicForm = this.state.basicForm;
        if(Object.keys(basicForm).length > 0){
            let param = new FormData();
            param.append("route",basicForm.route);
            param.append("method",basicForm.method);
            param.append("body",basicForm.body);
            fetch(`${contextUrl}/usou/cluster/${this.props.page.id}/_search/complex`, {
            credentials: 'same-origin',
            method: 'POST',
            body : param} )
                .then( ( res ) => res.json() )
                .then( ( data ) => {
                    if(data.status == '400'){
                        popup( <Snackbar message={`错误信息:${data.message}`} /> );
                    }else{
                        let { search } = data;
                        this.setState( { search} );
                        this.getQueryDataSource();
                        this.getQueryDataColumn();
                    }
                }).catch( ( err ) => console.log( err.toString() ) );
        }
    }
    //改变索引值
    changeSource(dataString){
        let basicIndex = dataString;
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/${dataString}/_mapping`, {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                
                let result = data[dataString].mappings;
                let selectSource = [],
                    metaSelect = ["match_all","_all"];
                    selectSource = selectSource.concat(metaSelect);
                Object.keys(result).map((index) => {
                    Object.keys(result[index].properties).map((item) => {
                        selectSource.push(item);
                    }); 
                });
                selectSource = [...new Set(selectSource)];
                let count = 1;
                let queryCount = [1];
                let basicSize = '10';
                let queryBool = [];
                this.setState( { selectSource,count,queryCount,basicIndex,basicSize,queryBool } );
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //改变基本查询每页显示条数
    changeBasicSize(dataString){
        let basicSize = dataString;
        this.setState({ basicSize });
    }
    afterBasicUpdate(data){
        let basicSearchShow = this.state.basicSearchShow;
        if(basicSearchShow == 'checked'){
            let getQueryBool = this.getQueryBool();
            let searchJson = JSON.parse(getQueryBool.body);
            $('#json-renderer').jsonViewer(searchJson, '');
        }

        if(data.status == '400'){
            popup( <Snackbar message={`错误信息:${data.message}`} /> );
        }else{
            let { search } = data;
            this.setState( { search} );
            this.getQueryDataSource();
            this.getQueryDataColumn();
        }
    }
    beforeBasicUpdate(dataString){
        let basicSubFlag = true;
        let basicSearchShow = dataString.pretty;
        this.setState({ basicSubFlag,basicSearchShow });

        let getQueryBool = this.getQueryBool();

        return getQueryBool;
    }
    //数据组装
    getQueryBool(){
        let dataString = {};
        let {queryBool,basicIndex,basicSize} = this.state;
        let bool = {
            must : [],
            must_not : [],
            should : []
        };
        queryBool.map((item,index) => {
            Object.keys(bool).map((boolItem,boolIndex) => {
                if(boolItem == item.group){
                    bool[boolItem].push(item.data);
                }
            });
        });
        let query = {
            bool : bool
        };
        let body = {
            query : query ,
            from : 0,
            size : basicSize,
            sort : [],
            aggs : {}
        };
        let index = basicIndex;
        dataString["body"] = JSON.stringify(body);
        dataString["index"] = index;
        return dataString;
    }
     //获取基本查询子组件数组
    queryGroup(dataString){
        let queryBool = this.state.queryBool;
        let queryData = dataString;
        let keyID = queryData.fieldId;
        queryBool.map( (item,index) => {
            if(keyID == item.fieldId){
                queryBool.splice(index,1);
            }
        });
        queryBool.push(queryData);
        this.setState({ queryBool });
    }
     //基本查询索引列表
    fetchSearchBasic(){
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_cat/indices?head=docs.count`, {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( basicData ) => {
                if(basicData.status == '200'){
                    let result = basicData.data;
                    let resultBasic = [];
                    Object.keys(result).map((index) => {
                        if(result[index].status == "open"){
                            let basic = {
                                index : result[index].index,
                                count : result[index]['docs.count']
                            }
                            resultBasic.push(basic);
                        }
                    });
                    let basicIndex = (resultBasic.length > 0) ? resultBasic[0].index : '';

                    this.setState({resultBasic,basicIndex});
                }else{
                    popup( <Snackbar message={`错误信息:${basicData.message}`} /> );
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //查询基础数据接口
    getFetchBasic(){
        let getQueryBool = this.getQueryBool();
        let param = new FormData();
        param.append("body",getQueryBool.body);
        param.append("index",getQueryBool.index);
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_search/common`, {
            credentials: 'same-origin',
            method : 'POST',
            body:param
        } )
            .then( ( res ) => res.json())
            .then( ( data ) => {
                if(data.status == '400'){
                    popup( <Snackbar message={`错误信息:${data.message}`} /> );
                }else{
                    let { search } = data;
                    this.setState( { search} );
                    this.getQueryDataSource();
                    this.getQueryDataColumn();
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //监测SQL
    changeSQL(option,data){
        let sqlVal = data;
        return this.setState({ sqlVal }) 
    }
    //搜索
    searchSql(){
        let {sqlVal,explainShow,sqlShow,sqlIndex } = this.state;
        explainShow = false;
        sqlShow = true;

        let param = new FormData();
        param.append("sql",sqlVal);
        param.append("index",sqlIndex);
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_sql`, {
            credentials: 'same-origin',
            method : 'POST',
            body : param
        } )
        .then( ( res ) => res.json())
        .then( ( sqldata ) => {
            if(sqldata.code){
                let  sqlError =sqldata.data;
                this.setState({sqlError,sqldata,sqlShow});
            }else{
                if(sqldata.search.list.length==0){
                    let  sqlError ="No result";
                    this.setState({sqlError});
                }
                let { search } = sqldata.search;
                let resultSearch = sqldata.search.list;
                let keyNames = sqldata.search.keys;
                //this.setState( { search,explainShow,sqlShow,sqldata } );
                this.setState({search,explainShow,sqlShow,sqldata,resultSearch,keyNames},()=> this.getQueryDataColumn())
            }
        })
        .catch( ( err ) => console.log( err.toString() ) );
    }
   
    //解析SQL
    explainSQL(){
        let {sqlVal,explainShow,sqlShow,sqlIndex } = this.state;
        explainShow = true;
        sqlShow = false;

        let param = new FormData();
        param.append("sql",sqlVal);
        param.append("index",sqlIndex);
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_sql/explain`, {
            credentials: 'same-origin',
            method : 'POST',
            body : param
        } )
        .then( ( res ) => res.json())
        .then( ( sqldata ) => {
            if(sqldata.status == '200'){
                let sqlExplain = sqldata.data;
                this.setState({ sqlExplain,explainShow,sqlShow });
            }else{
                this.setState({explainShow,sqlShow });
                popup( <Snackbar message={`错误信息:${sqldata.message}`} /> );
            }
        })
        .catch( ( err ) => console.log( err.toString() ) );
    }
    //添加单行下拉框
    addQuery(){
        let {queryCount,count} = this.state;
        count = count + 1;
        queryCount.push(count);
        this.setState({ queryCount ,count});
    }
    //删除单行下拉框
    delQuery(key){
        let {queryCount,count,queryBool} = this.state;
        count = count + 1;
        if(queryBool.length != 1){
           if(key != 1){
                queryCount.map((item,index) => {
                    if(key == item){
                        queryCount.splice(index,1);
                    }
                });
                queryBool.map((item,index) => {
                    if(key == item.fieldId){
                        queryBool.splice(index,1);
                    }
                });
            }else{
                queryCount.pop(queryCount.length - 1);
                queryBool.pop(queryBool.length - 1);
            }     
        }
        this.setState({ queryCount , count ,queryBool});
    }
    //清除数据
    getClear(flag){
        let resultSearch = [],keyNames = [],
        resultColumn = [],deleteAll = [],
        search = [], total = '', took = '';
        let tabFlag = flag;
        this.setState({keyNames, resultSearch, resultColumn, search,tabFlag,total,took,deleteAll});
        if(flag == 'query'){
            const {panelIndice,panelType,panelField} = this.state;
            this.getIndicesTable(panelIndice,panelType,panelField);
        }else if(flag == 'basic'){
            let basicSubFlag = this.state.basicSubFlag;
            basicSubFlag ? this.getFetchBasic() : this.fetchSearchBasic();
        }else if(flag == 'complex'){
            this.getComplexData();
        }else if(flag == 'sql'){
            let {explainShow,sqlShow} = this.state;
            if(explainShow){
                this.explainSQL();
            }
            if(sqlShow){
                this.searchSql();
            }
            if(!explainShow && !sqlShow){
                this.fetchSearchSql();
            }
        }
    }
    //sql索引列表
    fetchSearchSql(){
        fetch(`${Uniplatform.context.url}/usou/cluster/${this.props.page.id}/_cat/indices?head=docs.count`, {
            credentials: 'same-origin',
            method : 'POST'
        } )
            .then( ( res ) => res.json())
            .then( ( basicData ) => {
                if(basicData.status == '200'){
                    let result = basicData.data;
                    let resultsql = [];
                    Object.keys(result).map((index) => {
                        
                        if(result[index].status == "open"){
                            let basic = {
                                index : result[index].index,
                                count : result[index]['docs.count']
                            }
                            resultsql.push(basic);
                        }
                    });
                    let sqlIndex = (resultsql.length > 0) ? resultsql[0].index : '';

                    this.setState({resultsql,sqlIndex});
                }else{
                    popup( <Snackbar message={`错误信息:${basicData.message}`} /> );
                }
            }).catch( ( err ) => console.log( err.toString() ) );
    }
    //导出
    getJsonDown(){
        let {tabFlag,panelIndice,panelType,panelField,basicForm,basicIndex} = this.state;

        let body = '';
        let index = '';
        if(tabFlag == 'query'){
            if(panelIndice.length == 0){
                panelIndice = "_all";
            }else{
                panelIndice.map((item,indicIndex) =>
                    item
                ).join(",");
            }

            panelType = (panelType == "") ? "" : panelType;

            index = `${panelIndice}/${panelType}`;

            if(panelField.length > 0){
                let wildcard = [];
                panelField.map( (item,selIndex) => {
                    let wild = {};
                    wild["wildcard"] = item.fieldName;
                    wildcard.push(wild);
                });

                body = `{"query":{"bool":{"must":${JSON.stringify(wildcard)},"must_not":[],"should":[]}},"from":0,"size":20,"sort":[],"aggs":{},"version":true}`;
            } else {
                body = `{"query":{"bool":{"must":[],"must_not":[],"should":[]}},"from":0,"size":20,"sort":[],"aggs":{},"version":true}`;
            }
        }else if(tabFlag == 'basic'){
            if(basicIndex != ''){
                let getQueryBool = this.getQueryBool();
                index = getQueryBool.body;
                body = getQueryBool.index;
            }else{
                index = '';
                body = '';
            }
        }else if(tabFlag == 'complex'){
            if(Object.keys(basicForm).length > 0){
                index = basicForm.route;
                body = basicForm.body;
            }
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

    //SQL查询索引改变
    changeSqlIndex(dataString){
        let sqlIndex = dataString;
        this.setState({ sqlIndex });
    }

    render() {
        let {took,total,basicSearchShow,resultsql,sqlIndex,basicIndex,basicSize,queryCount,sqlError,sqldata,explainShow,sqlShow,aliasName,indicesName,indicesType,indicesField,resultSearch,resultColumn,body,updateVisible,panelIndice,panelType,panelField,source,sourceMessage,resultBasic,selectSource,sqlVal,sqlExplain} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Divider/>
                <Row style={{minHeight: '600px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/searchData_Sql'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{ normal: 20, small: 24, medium: 20, large: 20 }}>
                        <Row>
                            <NavigationBar code={'searchData_Sql'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                        </Row>
                        <Row>
                            <Row>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{paddingLeft:'0px'}}>
                                    <Label>搜索文档</Label>
                                    <Select value={sqlIndex} onChange={this.changeSqlIndex} search={ true }>
                                       {
                                            indicesName.map( (item, index) => {
                                                return (<Option key={index} value={item.value}>{item.value}</Option>)
                                            })
                                        }
                                    </Select>
                                    <Textarea rows={6} name="sql" value={sqlVal} onChange={this.changeSQL.bind(this,'sql')}/>
                                
                                    <Divider />
                                     <ButtonGroup style={{position:'relative',float:'right',zIndex:1}}>
                                        <Button type="default" onClick={this.searchSql.bind(this)}>搜索</Button>
                                        <Button type="default" onClick={this.explainSQL.bind(this)}>解析</Button>
                                    </ButtonGroup>

                                </Col>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0px'}}>
                                 <Row>
                            { 
                                explainShow ?
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{paddingLeft:'0px'}}>
                                    <h2>结果</h2>
                                    <Divider line={true}/>
                                    <Textarea rows={12} name="sqlexplain" value={util_formatJson(sqlExplain)}/>
                                </Col>
                                : ''
                            }
                            {
                                sqlShow ?
                                    sqldata.code?
                                    <Col size={{ normal: 24, small: 24, medium: 17, large: 17 }} style={{paddingLeft:'0px'}} className="sqlError">
                                        <Divider/>
                                        <Alert message={sqlError} type="error" />
                                    </Col>   
                                    :
                                    Object.keys(resultSearch).length > 0 ?
                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{paddingLeft:'0px'}}>
                                            <ButtonGroup style={{position:'relative',marginTop:'10px',zIndex:1}}>
                                                <Button type="default" onClick={this.deleteQuery.bind(this)}>删除</Button>
                                                <Button type="default" onClick={this.getJsonDown.bind(this) } >导出全部</Button>
                                                <Button type="default" onClick={this.getJsonByIdDown.bind(this) } >导出选中</Button>
                                            </ButtonGroup>
                                            <Divider/> 
                                            {
                                                (total != "") ?
                                                <Text>命中 {total} 个分片  耗时 {took} 秒</Text>
                                                : ''

                                            }
                                            <Divider/> 
                                            <Table textAlign="center" dataSource={resultSearch} columns={resultColumn} striped={ true } bordered={ true } checkable headBolder complex onCheck={ this.handleSelect }/>
                                        </Col>
                                        :
                                        <Col size={{ normal: 24, small: 24, medium: 17, large: 17 }} style={{paddingLeft:'0px'}} className="sqlError">
                                            <Divider/>
                                            <Alert message={sqlError} type="info" />
                                        </Col> 
                                : ''
                            }
                            </Row>
                                </Col>

                                <Divider />
                            </Row>
                        </Row>
                    </Col>
                </Row>

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
                <Footer/>
            </Page>
        );
    }

}

SearchData.UIPage = page;
export default SearchData;
