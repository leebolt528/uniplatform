import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,DateTime,Dialog,Notification,
    Tabs,Tab,Cascade,Transfer,Dropdown,Progress} from 'epm-ui';
/*import { Footer,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';*/

const contextUrl = '/uniplatform';

const page = {
    title: '数据集',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/dataSet.min.css'
    ]
};

class DataSet extends Component{
     constructor(props){
        super(props);

        this.state = {
           dataTypes:[],
           dataTypesIndex:0,
           dataTypeId:'',
           functionsTypes:[],
           functionsTypeIndex:0,
           functionsTypesId:'',
           bodyHtml:[],
           dataSetId:"",
           dataSetData:[],
           dataSet_index:"",
           sizeDataSet:9,
           indexDataSet:1,
           totalDataSet:0,
           indexObject:1,
           totalObject:0,
           size:10,
           addDataSetVisible:false,
           showDataSetBool:true,
           objectsData:[],
           objectsTransferData:[],
           typeFunction:[],
           corpusSetData:[],
           random:false,
           corpusMaxNum:-1,
           classifyId:'',
           pageStart:0,
           objectBool:false,
           onePageTransferBool:false,
           resultBool:true,
           resultSetBool:true,
           colorIndex:'Classify',
           objectId:'',
           corpusData:[],
           sizeCorpus:10,
           indexCorpus:1,
           totalCorpus:0,
           isUpload:true,
           corpusTransferVisible:false,
           leftList:[],
           rightList:[],
           leftListCheck:[],
           rightListCheck:[],
           editShowDataSet:'',
           selectDataId:[],
           selectAllDataId:[],
           pageBool:false,
           corpusSetId:'',
           rightListAll:[],
           addCorpusIds:[],
           removeCorpusIds:[],
           descriptionTransfer:'',
           higherLevelTransfer:'',
           titleTransfer:'',
           isUsedDataSet:0,
           progressBool:false,
           firstIn:true,
           searchTableBool:false
        }
    }
    componentDidMount() {
       this.fetchDataTypes();
    }
    //获取数据集类型
    fetchDataTypes(){
        let {dataTypes}=this.state;
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getCorpusType', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            dataTypes=data.corpusTypes;
            this.setState({dataTypes},()=>this.clickDataTypes(3));
        }).catch((err) => console.log(err.toString()));
    }
     //点击语料类型
    clickDataTypes(index){
        let {dataTypesIndex,dataTypes,dataTypeId,indexDataSet}=this.state;
        dataTypesIndex=index;
        dataTypeId=dataTypes[index].id;
        Object.keys(dataTypes).map((index) => 
           dataTypes[index].status=false
        );
        dataTypes[index].status=true;
        indexDataSet=1;
        this.setState({dataTypes,dataTypeId,dataTypesIndex,indexDataSet},()=>this.getFunctions());
    }
    //过滤分词语料功能
    clickFunctionsTypes(index){
        let {functionsTypeIndex,functionsTypesId,functionsTypes,indexDataSet,sizeDataSet,resultSetBool,firstIn,searchWordSet}=this.state;
        functionsTypeIndex=index;
        functionsTypesId=this.state.functionsTypes[index].id;
        Object.keys(functionsTypes).map((index) => 
           functionsTypes[index].status=false
        );
        functionsTypes[index].status=true;
        indexDataSet=1;
        if(firstIn==false){
            searchWordSet='';
            sizeDataSet=9;
            resultSetBool=true;
        }
        firstIn=false;
        this.setState({functionsTypesId,functionsTypeIndex,functionsTypes,indexDataSet,sizeDataSet,resultSetBool,firstIn,searchWordSet},()=>this.fetchDataSet());
    }
    //获取功能
    getFunctions(){
        let {dataTypesIndex,bodyHtml,dataTypes,sizeDataSet,resultSetBool,searchWordSet}=this.state;
        let param = new FormData();
        param.append('corpusTypeId',this.state.dataTypeId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getFunctions', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
             let functionsTypes=data.functions;
             let functionsTypesId=functionsTypes[0].id;
             let functionsTypeIndex=0;
             if(this.state.firstIn==false){
                searchWordSet='';
                sizeDataSet=9;
                resultSetBool=true;
             }
             this.setState({functionsTypes,functionsTypesId,functionsTypeIndex,sizeDataSet,resultSetBool,searchWordSet},()=>this.fetchDataSet());
           /*  let bodyHtml=[];
            switch(dataTypesIndex){
                case 0:
                    bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</Button> 
                                        )
                                    })
                                }
                            </Col>
                            <div className="corpusManage_content">
                                {dataTypes[0].name}
                            </div>
                        </div>
                    );
                    break;
                case 1:
                    bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</Button> 
                                        )
                                    })
                                }
                            </Col>
                            <div className="corpusManage_content">
                                {dataTypes[1].name}
                            </div>
                        </div>
                    );
                    break;
                case 2:
                    let functionsTypes=data.functions;
                    let functionsTypesId=functionsTypes[0].id;
                    let functionsTypeIndex=0;
                    this.setState({functionsTypes,functionsTypesId,functionsTypeIndex},()=>this.fetchDataSet());
                    break;
                default:
                    bodyHtml.push("default");
                    break;

           };
           this.setState({bodyHtml}); */
        }).catch((err) => console.log(err.toString()));
    }
    //获取文本分类（数据集）
     fetchDataSet(){
        let {functionsTypesId,sizeDataSet,indexDataSet,totalDataSet,dataSetData,showDataSetBool,resultSetBool}=this.state;
        let param = new FormData();
        param.append('functionId',functionsTypesId);
        param.append('pageIndex',indexDataSet);
        param.append('pageSize',sizeDataSet);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/listDataSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            dataSetData=data.dataSet;
            totalDataSet=data.dataSetNum;
            resultSetBool=true;
            this.setState({dataSetData,totalDataSet,resultSetBool},()=>{if(showDataSetBool){this.showDataSet()}});
        }).catch((err) => console.log(err.toString()));
    }
    //展示数据集内容
    showDataSet(){
         let {functionsTypeIndex,functionsTypes,bodyHtml,dataSetData,dataTypes,indexDataSet,totalDataSet,sizeDataSet,resultSetBool,searchWordSet}=this.state;
         bodyHtml=[];
         switch(functionsTypeIndex){
                case 0 :
                    bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <a type="default" key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</a> 
                                        )
                                    })
                                }
                                <br/>
                                 <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <a type="default" key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</a> 
                                        )
                                    })
                                }
                                <Input placeholder="请输入要查询的词" type="search"
                                    style={{float:'right'}}
                                    value={searchWordSet}
                                    onKeyPress={ this.wordSearchSet.bind(this)}
                                    getter={ this.getWordInputSet.bind(this) }
                                    onChange={this.wordSearchAutoSet.bind(this)}>
                                    <Input.Left icon="search" />
                                </Input>
                            </Col>
                            <div className="corpusManage_content">
                                <Row>
                                    {   
                                        dataSetData.length>0?
                                        dataSetData.map((value,index)=>{
                                            return(
                                                <Col style={{width:'20%'}} key={index}>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col" style={{background:'#98d37b'}}>
                                                        <div className = "corpusManage_title">
                                                            <Dropdown position={'bottom'}>
                                                                <Dropdown.Trigger action="hover">
                                                                    <span className = "corpusManage_span" style={{height:'35px',textAlign:'center'}} >{value.dataSetName}</span>
                                                                </Dropdown.Trigger>
                                                                <Dropdown.Content>
                                                                    <div style={ { width: '200px', maxHeight:'200px',overflow:'auto' } }>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>数据集名称：{value.dataSetName}</p>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>体系：{value.classifyName}</p>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>语量：{value.corpusNum}</p>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>状态：{value.status==1?"已使用":"未使用"}</p>
                                                                        {
                                                                            value.isUpload?
                                                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>创建方式：上传</p>
                                                                            :
                                                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>语料集：{value.coupusSetName}</p>
                                                                        }
                                                                    </div>
                                                                </Dropdown.Content>
                                                            </Dropdown>
                                                        </div>
                                                    </Col>
                                                    <div className="corpusManage_content hang">
                                                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cent" style={{height:'156px',zIndex:'1000',cursor:'pointer'}}>
                                                            <p>
                                                                <span style={{fontSize:'14px'}}>体系：</span>
                                                                <span style={{fontSize:'16px',color:'#b67561'}}>{value.classifyName}</span>
                                                            </p>
                                                            <p>
                                                                <span style={{fontSize:'14px'}}>语量：</span>
                                                                <span style={{fontSize:'16px',color:'#b67561'}}>{value.corpusNum}</span>
                                                            </p>
                                                            <p>
                                                                <span style={{fontSize:'14px'}}>状态：</span>
                                                                <span style={{fontSize:'16px',color:'#b67561'}}>{value.status==1?"已使用":"未使用"}</span>
                                                            </p>
                                                            {
                                                                value.isUpload?
                                                                <p>
                                                                    <span style={{fontSize:'14px'}}>创建方式：</span>
                                                                    <span style={{fontSize:'16px',color:'#b67561'}}>上传</span>
                                                                </p>
                                                                :
                                                                <p>
                                                                    <span style={{fontSize:'14px'}}>语料集：</span>
                                                                    <span style={{fontSize:'16px',color:'#b67561'}}>{value.coupusSetName}</span>
                                                                </p>
                                                            }
                                                            <div className="layer"></div>
                                                            {
                                                                value.status==1?
                                                                <div className="layer_but">
                                                                    <a style={{backgroundColor: 'transparent', margin: '2%',cursor: 'not-allowed',opacity:'0.7'}}>
                                                                        <img src="../image/del.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%'}} onClick={this.downDataSet.bind(this,value)}>
                                                                        <img src="../image/down.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%',cursor: 'not-allowed',opacity:'0.7'}}>
                                                                        <img src="../image/edit.png" />
                                                                    </a>
                                                                    <Divider />
                                                                    <Button type="info" block style={{backgroundColor:'#6fccf5',color:'white'}} onClick={this.clickDataSet.bind(this,index)}>
                                                                        详情
                                                                    </Button>
                                                                </div>
                                                                :
                                                                <div className="layer_but">
                                                                    <a style={{backgroundColor: 'transparent', margin: '2%'}} onClick={this.delDataSet.bind(this,value)}>
                                                                        <img src="../image/del.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%',cursor:`${value.corpusNum==0?'not-allowed':''}`,opacity:`${value.corpusNum==0?'0.7':'1'}`}}  onClick={this.downDataSet.bind(this,value)}>
                                                                        <img src="../image/down.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%'}} onClick={this.editDataSet.bind(this,value)}>
                                                                        <img src="../image/edit.png" />
                                                                    </a>
                                                                    <Divider />
                                                                    <Button type="info" block style={{backgroundColor:'#6fccf5',color:'white'}} onClick={this.clickDataSet.bind(this,index)}>
                                                                        详情
                                                                    </Button>
                                                                </div>
                                                            }
                                                        </Col>
                                                    </div>
                                                </Col>
                                            )
                                        })
                                        :
                                        ""
                                    }
                                    {
                                        resultSetBool?
                                        <Col style={{width:'20%'}} key="">
                                            <div style={{height:'200px',border:'dashed 1px #d8dde8',borderRadius:'20px',textAlign:'center'}}>
                                                <a type="default" size="tiny"
                                                style={{margin: '10px auto', position: 'relative', zIndex: 1}}
                                                onClick={this.showAddDataSetModal.bind(this)}
                                                >
                                                    <Icon icon="plus" className="plus fa-5x" style={{margin:'28% 0px',height:'74px',width:'74px',color:'#d8dde8'}}/>
                                                </a>
                                            </div>
                                        </Col>
                                        :
                                        dataSetData.length>0?
                                            ""
                                            :
                                            <span className="nullContent">不存在对应的数据集</span>
                                    }
                                </Row>
                                <Row>
                                    <Pagination index={indexDataSet}  total={totalDataSet} size={sizeDataSet} onChange={this.exchangePageDataSet.bind(this)} align='center'/>
                                </Row>
                                <Divider />
                            </div>
                        </div>
                    );
                    break;
                case 1:
                   bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</a> 
                                        )
                                    })
                                }
                                <br/>
                                 <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</a> 
                                        )
                                    })
                                }
                            </Col>
                            <div className="corpusManage_content">
                                {functionsTypes[functionsTypeIndex].displayName}
                            </div>
                        </div>
                    );
                    break;
                case 2:
                    bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</a> 
                                        )
                                    })
                                }
                                <br/>
                                 <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</a> 
                                        )
                                    })
                                }
                            </Col>
                            <div className="corpusManage_content">
                                {functionsTypes[functionsTypeIndex].displayName}
                            </div>
                        </div>
                    );
                    break;
                case 3 :
                   bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</a> 
                                        )
                                    })
                                }
                                <br/>
                                 <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</a> 
                                        )
                                    })
                                }
                            </Col>
                            <div className="corpusManage_content">
                                {functionsTypes[functionsTypeIndex].displayName}
                            </div>
                        </div>
                    );
                    break;
                default:
                    bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">类型：</label></span>
                                {
                                    dataTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${dataTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickDataTypes.bind(this,index)}>{value.name}</a> 
                                        )
                                    })
                                }
                                <br/>
                                 <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <a key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</a> 
                                        )
                                    })
                                }
                            </Col>
                            <div className="corpusManage_content">
                              "default"
                            </div>
                        </div>
                    );
                    break;
            };
        this.setState({bodyHtml});
    }
    //分页
    exchangePageDataSet(indexDataSet){
        this.setState({indexDataSet},()=>this.fetchDataSetPage());
    }
    fetchDataSetPage(){
        if(this.state.resultSetBool){
            this.fetchDataSet();
        }else{
            this.searchDataSet();
        }
    }
    exchangePageObject(indexObject){
        let {indexCorpus,pageBool}=this.state;
        indexCorpus=1;
        pageBool=true;
        this.setState({indexObject,indexCorpus,pageBool},()=>this.fetchObject());
    }
    exchangePageCorpus(indexCorpus,sizeCorpus){
        let pageStart=indexCorpus-1;
        this.setState({indexCorpus,pageStart,sizeCorpus},()=>{this.state.searchTableBool?this.searchCorpus():this.fetchCorpus()});
    }
    fetchCorpus(){
        this.state.objectBool?this.fetchCorpusObject(true):this.fetchCorpusDataSet();
    }
    //数据集操作
    delDataSet(value){
        let dataSetId=value.dataSetId;
        this.setState({dataSetId});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleDataSetApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此数据集？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确认删除数据集
    handleDataSetApprove(after){
        let {dataSetId,totalDataSet,sizeDataSet,indexDataSet,}=this.state;
         let param = new FormData();
        param.append('dataSetId',dataSetId);
         fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/deleteDataSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                if(totalDataSet%sizeDataSet==1&&indexDataSet==Math.ceil(totalDataSet/sizeDataSet) ){
                    this.setState({indexDataSet:1});
                }
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchDataSetPage() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
        after( true );
    }
    downDataSet(value){
         let {objectBool,objectId,objectsData,colorIndex}=this.state;
        let dataSetId=value.dataSetId;
        if(objectBool){
            if(objectsData[colorIndex].courpusNum== 0){
                popup( <Snackbar message={`该对象不存在语料`} /> );
            }else{
                window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/dataSet/downloadDataSet?dataSetId=${dataSetId}&objectId=${objectId}`,'_self');
            } 
        }else{
            if(value.corpusNum == 0){
                popup( <Snackbar message={`该数据集不存在语料`} /> );
            }else{
                window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/dataSet/downloadDataSet?dataSetId=${dataSetId}`,'_self');
            } 
        }
    }
    trim(s){
        return s.replace(/(^\s*)|(\s*$)/g, "");
    }
    //编辑数据集
    editDataSet(value){
        let editShowDataSet=value;
        this.setState({ editDataSetVisible : true,editShowDataSet });
    }
    closeEditDataSet(){
        this.resetEdit();
        this.setState({ editDataSetVisible : false });
    }
    beforeEditDataSetSubmit(data){
        data.dataSetId=this.state.editShowDataSet.dataSetId;
         data.name=this.trim(data.name);
        if(data.name==""){
            popup(<Snackbar message="请输入新的数据集名称！"/>) 
            return;
        }
        data.functionId=this.state.functionsTypesId;
        return data;
    }
    afterEditDataSetSubmit(data){
        this.resetEdit();
        this.setState({ editDataSetVisible : false });
        data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchDataSetPage(): popup(<Snackbar
        message={data.msg}/>)
    }
    //数据集详情
    clickDataSet(index){
        let {dataSetId,dataSetData,dataSet_index,classifyId,objectBool,indexObject,showDataSetBool,isUsedDataSet}=this.state;
        dataSet_index=index;
        isUsedDataSet=dataSetData[dataSet_index].status;
        dataSetId=dataSetData[dataSet_index].dataSetId;
        classifyId=dataSetData[dataSet_index].classifyId;
        objectBool=false;
        indexObject=1;
        showDataSetBool=false;
        this.setState({dataSet_index,dataSetId,classifyId,objectBool,indexObject,showDataSetBool,isUsedDataSet},()=>this.fetchObject());
    }
    //获取数据集对象
    fetchObject(){
        let {indexObject,totalObject,size,dataSetId,objectsData,classifyId,colorIndex,objectId,isUpload,pageBool,corpusSetId,objectsTransferData}=this.state;
        let param = new FormData();
        param.append('dataSetId',dataSetId);
        param.append('pageIndex',indexObject);
        param.append('pageSize',size);
        param.append('classifyId',classifyId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/listDataSetDetail', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            objectsData=data.detail;
            objectsTransferData=[];
            objectsTransferData.push({objectId:`${dataSetId}`,objectName:'全部',corpusNum:'全部'});
            objectsData.map((value)=>{
                objectsTransferData.push(value);
            });
            totalObject=data.totalNum;
            isUpload=data.isUpload;
            corpusSetId=data.corpusSetId;
            if(colorIndex!=="Classify"&&pageBool==true){
                colorIndex=0;
                objectId=objectsData[0].objectId;
            }
            this.setState({objectsData,totalObject,colorIndex,objectId,isUpload,corpusSetId,objectsTransferData},()=>{this.state.searchTableBool?this.searchCorpus():this.fetchCorpus()});
        }).catch((err) => console.log(err.toString())); 
    }
    //获取数据集下的所有语料
    fetchCorpusDataSet(needText){
        let {indexCorpus,corpusData,sizeCorpus,dataSetId,totalCorpus,onePageTransferBool,rightList,rightListAll}=this.state;
        if(onePageTransferBool){
            indexCorpus=1;
            sizeCorpus=100000;
        }
        let param = new FormData();
        param.append('dataSetId',dataSetId);
        param.append('pageIndex',indexCorpus);
        param.append('pageSize',sizeCorpus);
        param.append('needText',`${needText==false?false:true}`);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/listDataSetCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            corpusData=data.corpus;
            rightList=[];
            corpusData.map((value)=>{
                var temp = {
                    text: `${ value.title }`,
                    key: `${ value._id+','+value.higherLevel+','+value.title+','+value.higherLevelId}`,
                    higherLevel:`${ value.higherLevel }`
                };
                rightList.push( temp );
            });
            rightListAll=rightList;
            this.handleSelectFetch(corpusData);
            totalCorpus=data.totalNum;
            this.setState({corpusData,totalCorpus,rightList,rightListAll},()=>this.showObjects());
        }).catch((err) => console.log(err.toString())); 
    }
    //通过关键字检索数据集
    getWordInputSet(getter){
        this.getWordValueSet=getter.value;
    }
    wordSearchSet(event){
        let {resultSetBool}=this.state;
        let indexDataSet=1;
        if (event.which==13){
            let searchWordSet = this.getWordValueSet();
            if(searchWordSet==''){
                resultSetBool=true;
            }else{
                resultSetBool=false;
            }
            this.setState({searchWordSet,indexDataSet,resultSetBool}, () => this.searchDataSet());
        }
    }
    wordSearchAutoSet(data){
        let indexDataSet=1;
        if(data==""){
            let resultSetBool=true;
            this.setState({searchWordSet:"",resultSetBool,indexDataSet}, () => this.searchDataSet());
        }
    }
    searchDataSet(){
        let {size,sizeDataSet,indexDataSet,searchWordSet,functionsTypesId}=this.state;
        if(searchWordSet==''){
            sizeDataSet=9;
        }else{
            sizeDataSet=size;
        }
        let param = new FormData();
        param.append('pageSize',sizeDataSet);
        param.append('pageIndex',indexDataSet);
        param.append('keyWord',searchWordSet);
        param.append('functionId',functionsTypesId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/searchDataSetName', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let dataSetData=data.corpus;
            let totalDataSet=data.totalNum;
            this.setState({dataSetData,totalDataSet,sizeDataSet},()=>this.showDataSet());
        }).catch((err) => console.log(err.toString()));
    }
    //检索关键字
    getWordInput(getter){
        this.getWordValue=getter.value;
    }
    wordSearch(event){
        let indexCorpus=1;
        if (event.which==13){
            let searchWord = this.getWordValue();
            let sizeCorpus=10;
            let searchTableBool=true;
            let pageStart=0;
            this.setState({searchWord,indexCorpus,sizeCorpus,searchTableBool,pageStart}, () => this.searchCorpus());
        }
    }
    wordSearchAuto(data){
        if(data==""){
            let indexCorpus=1;
            let sizeCorpus=10;
            let pageStart=0;
            this.setState({searchWord:"",sizeCorpus,indexCorpus,pageStart}, () => this.searchCorpus());
        }
    }
     searchCorpus(){
        let {sizeCorpus,indexCorpus,searchWord,dataSetId,objectId,corpusData,totalCorpus,resultBool}=this.state;
        let param = new FormData();
        param.append('pageSize',sizeCorpus);
        param.append('pageIndex',indexCorpus);
        param.append('keyWord',searchWord);
        param.append('dataSetId',dataSetId);
        param.append('objectId',objectId);
        param.append('needText ',true);

        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/searchDataSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            corpusData=data.corpous;
            totalCorpus=data.totalNum;
            resultBool=false;
            this.setState({ corpusData,totalCorpus,resultBool},()=>this.showObjects());
        }).catch((err) => console.log(err.toString()));
    }
    //批量选择表格数据
    handleSelect(data,currentData){
        let {selectDataId,selectAllDataId}=this.state;
        if(selectAllDataId.length==data.length){
            data.map((value)=>{
                selectDataId.push(value._id+':'+value.higherLevelId);
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
                if(selectDataId.indexOf(currentData._id+':'+currentData.higherLevelId)!==-1){
                    let index=selectDataId.indexOf(currentData._id+':'+currentData.higherLevelId);
                    selectDataId.splice(index,1);
                }else{
                    selectDataId.push(currentData._id+':'+currentData.higherLevelId);
                }
            }
        }
        this.setState({selectDataId});
    }
    handleSelectFetch(corpusData){
        let {selectDataId,selectAllDataId}=this.state;
        selectAllDataId=[];
        if(selectDataId.length>0){
            selectDataId.map((value1)=>{
                corpusData.map((value2)=>{
                    if(value1==(value2._id+':'+value2.higherLevelId)){
                        value2.checked=true;
                    }
                });
            });
        };
        if(corpusData.length>0){
            corpusData.map((value)=>{
                selectAllDataId.push(value._id+':'+value.higherLevelId);
            });
        };
        this.setState({selectAllDataId});
    }
    //表格操作
    getJsonByIdDown(){
        let {selectDataId} = this.state;
        if(selectDataId.length==0){
            popup( <Snackbar message={`没有选择要导出的语料`} /> );
        }else{
            this.clearSelectData();
            window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/dataSet/downloadDataSet?dataSetId=${this.state.dataSetId}&corpusIds=${selectDataId}`,'_self');        } 
    }
     //清空表格选中数据及状态
    clearSelectData(){
        let {corpusData} = this.state;
        corpusData.map((value)=>{
            value.checked=false;
        });
        this.setState({selectData:'',selectDataId:[],corpusData},()=>this.showObjects());
    }
    deleteCorpusOne(value){
        let selectDataId=[];
        selectDataId.push((value._id+':'+value.higherLevelId));
        this.setState({selectDataId});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleCorpusOneApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此语料？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确定删除语料
    handleCorpusOneApprove(after){
        let {selectDataId,dataSetId,indexCorpus,pageStart,totalCorpus,size}=this.state;
        let param = new FormData();
        param.append("dataSetId", dataSetId);
        param.append("corpusIds", selectDataId);
        fetch( Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/deleteCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body : param
            })
            .then((res) => res.json())
            .then((data) => {
                if(totalCorpus%size==1&&indexCorpus==Math.ceil(totalCorpus/size) ){
                    this.setState({indexCorpus:1,pageStart:0});
                }
                this.setState({selectDataId:[]});
                data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.updateSuccess(): popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
            });
        after( true );
    }
    //批量删除语料
    deleteCorpus(){
        let {selectDataId}=this.state;
        if(selectDataId.length==0){
            popup( <Snackbar message={`没有选择要删除的语料`} /> );
            return false; 
        } 
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleCorpusApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除所选语料？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确认批量删除语料
    handleCorpusApprove(after){
        let {selectDataId,dataSetId,indexCorpus,pageStart,totalCorpus,size}=this.state;
        let param = new FormData();
        param.append("dataSetId", dataSetId);
        param.append("corpusIds", selectDataId);
        fetch( Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/deleteCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body : param
            })
            .then((res) => res.json())
            .then((data) => {
                let index11=Math.ceil((totalCorpus-selectDataId.length)/size);
                if(index11<indexCorpus){
                    this.setState({indexCorpus:1,pageStart:0});
                }else{
                    this.setState({indexCorpus});
                }
                this.setState({selectDataId:[]});
                data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.updateSuccess(): popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
            });
        after( true );
    }
    updateSuccess(){
        this.fetchObject();
        this.fetchDataSet();
    }
    getJsonOneDown(value){
        let selectDataId=value._id;
        window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/corpusSet/exportOneCorpus?corpusId=${selectDataId}`,'_self');
    }
    //查看语料详情
    viewCorpus(value,index){
        let {corpusData}=this.state;
        if(document.getElementsByClassName('notification')[0]){
            document.getElementsByClassName('notification')[0].style.visibility="visible";
        }
        this.text = <Notification 
                message={"分类标签："+corpusData[index].higherLevel}
                description={value}
                duration={ 0 }
                position="topLeft"
                key={ Math.random().toString() }
                onDismiss={this.closeNotification.bind(this)}
             />;
        popup( this.text );
    }
     viewCorpus1(viewCorpus){
        let {corpusData}=this.state;
        if(document.getElementsByClassName('notification')[0]){
            document.getElementsByClassName('notification')[0].style.visibility="visible";
        }
         let param = new FormData();
            param.append('corpusId',viewCorpus.split(',')[0]);
         fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getCorpusText', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                data.status == '200' ?this.showText1(data,viewCorpus) : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
    }
    showText1(data,viewCorpus){
        this.setState({descriptionTransfer:data.text,higherLevelTransfer:viewCorpus.split(',')[1],titleTransfer:viewCorpus.split(',')[2]});
    }
    closeNotification(){
        document.getElementsByClassName('notification')[0].style.visibility="hidden";
    }
    //展示对象
    showObjects(){
        let {bodyHtml,dataSetData,dataSet_index,objectsData,indexObject,totalObject,size,sizeCorpus,corpusData,indexCorpus,totalCorpus,objectBool,resultBool,colorIndex,isUpload,isUsedDataSet}=this.state;
        bodyHtml=[];
        bodyHtml.push(
            <div key="">
                <div className="corpusManage_content">
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.downDataSet.bind(this,dataSetData[dataSet_index])} disabled={eval(`${dataSetData[dataSet_index].corpusNum==0||(colorIndex=='Classify'?false:objectsData[colorIndex].corpusNum== 0)}`)}>
                            <i className="fa fa-download"></i> 导出语料
                        </Button>
                        <Button type="default" size="medium" style={{float:'right',display:`${isUpload==true?"none":"inline-block"}`}} onClick={this.corpusTransfer.bind(this)}>
                            <i className="fa fa-exchange"></i> 选择语料
                        </Button>
                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.quitObjectShow.bind(this)}>
                            <i className="fa fa-reply"></i> 返回
                        </Button>
                    </Col>
                    <Col style={{width:'20%'}}>
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col" style={{background:'#98d37b'}}>
                            <div className = "corpusManage_title">
                                 <Dropdown position={'bottom'}>
                                    <Dropdown.Trigger action="hover">
                                        <span className = "corpusManage_span" style={{height:'35px',textAlign:'center'}} >{dataSetData[dataSet_index].dataSetName}</span>
                                    </Dropdown.Trigger>
                                    <Dropdown.Content>
                                        <div style={ { width: '200px', maxHeight:'200px',overflow:'auto' } }>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>数据集名称：{dataSetData[dataSet_index].dataSetName}</p>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>体系：{dataSetData[dataSet_index].classifyName}</p>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>语量：{dataSetData[dataSet_index].corpusNum}</p>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>状态：{dataSetData[dataSet_index].status==1?"已使用":"未使用"}</p>
                                            {
                                                dataSetData[dataSet_index].isUpload?
                                                <p style={{marginLeft:'20px',marginRight:'20px'}}>创建方式：上传</p>
                                                :
                                                <p style={{marginLeft:'20px',marginRight:'20px'}}>语料集：{dataSetData[dataSet_index].coupusSetName}</p>
                                            }
                                        </div>
                                    </Dropdown.Content>
                                </Dropdown>
                            </div>
                        </Col>
                        <div className="corpusManage_content hang" onClick={this.clickInDataSet.bind(this)} style={{backgroundColor:`${colorIndex==="Classify"?"rgb(206, 238, 252)":""}`,border:`${colorIndex==="Classify"?"1px solid rgb(111, 204, 245)":""}`}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cent" style={{height:'156px',zIndex:'1000',cursor:'pointer'}}>
                                <p>
                                    <span style={{fontSize:'14px'}}>体系：</span>
                                    <span style={{fontSize:'16px',color:'#b67561'}}>{dataSetData[dataSet_index].classifyName}</span>
                                </p>
                                <p>
                                    <span style={{fontSize:'14px'}}>语量：</span>
                                    <span style={{fontSize:'16px',color:'#b67561'}}>{dataSetData[dataSet_index].corpusNum}</span>
                                </p>
                                <p>
                                    <span style={{fontSize:'14px'}}>状态：</span>
                                    <span style={{fontSize:'16px',color:'#b67561'}}>{dataSetData[dataSet_index].status==1?"已使用":"未使用"}</span>
                                </p>
                                {
                                    dataSetData[dataSet_index].isUpload?
                                    <p>
                                        <span style={{fontSize:'14px'}}>创建方式：</span>
                                        <span style={{fontSize:'16px',color:'#b67561'}}>上传</span>
                                    </p>
                                    :
                                    <p>
                                        <span style={{fontSize:'14px'}}>语料集：</span>
                                        <span style={{fontSize:'16px',color:'#b67561'}}>{dataSetData[dataSet_index].coupusSetName}</span>
                                    </p>
                                }
                            </Col>
                        </div>
                    </Col>
                    { 
                        objectsData.length>0?
                        <div>
                            <Col style={{width:'5%',marginTop:'90px',padding:'0px',textAlign:'center'}}>
                                <img src="../image/rule_divider.svg" style={{width:'100%',display:'inline-block'}}/>
                            </Col>
                            <Col style={{width:'75%'}}>
                                {
                                    objectsData.map((value,index)=>{
                                        return(
                                            <Col style={{width:'20%'}} key={index}>
                                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col" style={{background:'#98d37b'}}>
                                                    <div className = "corpusManage_title">
                                                        <span className = "corpusManage_span" style={{height:'35px',textAlign:'center'}} >{value.objectName}</span>
                                                    </div>
                                                </Col>
                                                <div className="corpusManage_content hang" onClick={this.clickObject.bind(this,index)} style={{backgroundColor:`${colorIndex===index?"rgb(206, 238, 252)":""}`,border:`${colorIndex===index?"1px solid rgb(111, 204, 245)":""}`}}>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cent" style={{height:'156px',zIndex:'1000',cursor:'pointer'}}>
                                                        <p>
                                                            <span style={{fontSize:'14px'}}>数量：</span>
                                                            <span style={{fontSize:'16px',color:'#b67561'}}>{value.corpusNum}</span>
                                                        </p>
                                                    </Col>
                                                </div>
                                            </Col>
                                        )
                                    })
                                }
                            </Col>
                        </div>
                        :
                        <span className="nullContentTop">该数据集下不存在对象</span>
                    }
                    <Col>
                        <Pagination index={indexObject}  total={totalObject} size={size} align='center' onChange={this.exchangePageObject.bind(this)}/>
                        <Divider />
                    </Col>
                </div>
                <div>
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col">
                        <div className = "corpusManage_title" >
                            <span className = "corpusManage_span"></span>
                        </div>
                    </Col>
                    <div className="corpusManage_content">
                        <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                            <Input placeholder="请输入要查询的词" type="search"
                                style={{float:'left'}}
                                onKeyPress={ this.wordSearch.bind(this)}
                                getter={ this.getWordInput.bind(this) }
                                onChange={this.wordSearchAuto.bind(this)}
                                trigger={ this.searchTirggerRule.bind(this) }>
                                <Input.Left icon="search" />
                            </Input>
                            <Button type="default" size="medium" style={{float:'right'}} disabled={eval(`${corpusData.length==0||isUsedDataSet==1}`)} onClick={this.deleteCorpus.bind(this)} >
                                <i className="fa fa-trash"></i> 批量删除
                            </Button>
                            <Button type="default" size="medium" style={{float:'right'}} disabled={eval(`${corpusData.length==0}`)} onClick={this.getJsonByIdDown.bind(this)}>
                                <i className="fa fa-download"></i> 导出语料
                            </Button>
                            <Button type="default" size="medium" style={{float:'right',visibility:`${isUpload==true?"hidden":"visible"}`}} onClick={this.corpusTransfer.bind(this)}>
                                <i className="fa fa-exchange"></i> 选择语料
                            </Button>
                        </Col>
                        {
                            corpusData.length>0?
                            <Col>
                                <div className="ruleManage_content">
                                    <Row>
                                        <Table dataSource={ corpusData } headBolder style={{color:'#54698d'}} checkable={ true } striped={true} className="table_head" onCheck={ this.handleSelect.bind(this) }>
                                            <Column title="序号" scaleWidth = '7%' textAlign="center">
                                                { ( value, index ) => { return Number(`${Number(this.state.pageStart)*Number(this.state.sizeCorpus)+Number(index)}`) + 1; } }
                                            </Column>
                                            <Column title="对象名称" dataIndex="higherLevel" scaleWidth = '15%'textAlign="center"></Column>
                                            <Column title="语料内容" dataIndex="text" scaleWidth = '33%'textAlign="center">
                                                { ( value, index ) => { return <p style={{overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap',cursor:'pointer'}} onClick={this.viewCorpus.bind(this,value,index)}>{value}</p> }}
                                            </Column>
                                            <Column title="更新时间"  scaleWidth = '20%'textAlign="center">
                                                { ( value, index ) => { return <DateTime format='yyyy/MM/dd hh:mm:ss' content={value.update_time} style={{textAlign:'center'}}/> } }
                                            </Column>
                                            <Column title="操作" scaleWidth = '20%' textAlign="center">
                                                { ( value ) => {
                                                    return (
                                                        <div>
                                                        {
                                                            isUsedDataSet==1?
                                                            <div>
                                                                <Button type="default" size="tiny" disabled>
                                                                    删除
                                                                </Button>
                                                                <Button type="default" size="tiny" onClick={this.getJsonOneDown.bind(this,value)}>
                                                                    导出
                                                                </Button>
                                                            </div>
                                                            :
                                                            <div>
                                                                <Button type="default" size="tiny" onClick={this.deleteCorpusOne.bind(this,value)}>
                                                                    删除
                                                                </Button>
                                                                <Button type="default" size="tiny" onClick={this.getJsonOneDown.bind(this,value)}>
                                                                    导出
                                                                </Button>
                                                            </div>
                                                        }
                                                        </div>
                                                    )
                                                } }
                                            </Column>
                                        </Table>
                                        <Divider />
                                        <Pagination index={ indexCorpus }  total={totalCorpus} size={ sizeCorpus } onChange={this.exchangePageCorpus.bind(this)} align='center' showDataSizePicker dataSizePickerList={['10','50','100','200']}/>
                                    </Row>
                                    <Divider />
                                </div>
                            </Col>
                            :
                            resultBool==true?
                                objectBool?
                                <span className="nullContent">该对象不存在语料</span>
                                :
                                <span className="nullContent">该数据集不存在语料</span>
                            :
                            <span className="nullContent">不存在对应的语料</span>
                        }
                    </div>
                </div>
            </div>
        );
        this.setState({bodyHtml});
    } 
    //点击对象页面数据集
    clickInDataSet(){
        let {colorIndex,objectBool,indexObject,indexCorpus,objectId,sizeCorpus,searchTableBool}=this.state;
        colorIndex='Classify';
        objectBool=false;
        indexObject=1;
        indexCorpus=1;
        objectId="";
        sizeCorpus=10;
        searchTableBool=false;
        this.resetSearchRule();
        this.setState({colorIndex,objectBool,indexObject,indexCorpus,objectId,sizeCorpus,searchTableBool},()=>this.fetchObject());
    }
    //点击对象
    clickObject(index){
        let {colorIndex,objectId,objectsData,objectBool,indexCorpus,pageBool,sizeCorpus,searchTableBool}=this.state;
        objectId=objectsData[index].objectId;
        colorIndex=index;
        objectBool=true;
        indexCorpus=1;
        pageBool=false;
        sizeCorpus=10;
        searchTableBool=false;
        this.resetSearchRule();
        this.setState({colorIndex,objectId,objectBool,indexCorpus,pageBool,sizeCorpus,searchTableBool},()=>this.fetchCorpusObject(true));
    }
    //返回
    quitObjectShow(){
        let {colorIndex,showDataSetBool,sizeCorpus,searchTableBool}=this.state;
        colorIndex='Classify';
        showDataSetBool=true;
        sizeCorpus=10;
        searchTableBool=false;
        this.setState({colorIndex,showDataSetBool,sizeCorpus,searchTableBool},()=>this.fetchDataSetPage());
    }
    //添加数据集
    showAddDataSetModal(){
        this.fetchTypeFunc();
        this.setState({ addDataSetVisible : true,progressBool:false});
    }
    //获取新建数据集类型功能二级
    fetchTypeFunc(){
         fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/listTypeAndFunction', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            let typeFunction= Object.keys(data.data).map((value,index)=>{
                let child=data.data[value]["functions"].map((value)=>{
                    return {
                        name:value.functionName,
                        data:{value:value.functionId}
                    };
                });
                let typeFunc={
                    name:data.data[value].corpusTypeName,
                    data:{value:value},
                    children:child
                }
                return typeFunc;
            });
            this.setState({typeFunction});
        }).catch((err) => console.log(err.toString()));
    }
    //获取对象下的语料
    fetchCorpusObject(needText){
        let {indexCorpus,corpusData,sizeCorpus,dataSetId,totalCorpus,objectId,rightList,onePageTransferBool,rightListAll}=this.state;
        if(onePageTransferBool){
            indexCorpus=1;
            sizeCorpus=100000;
        }
        let param = new FormData();
        param.append('dataSetId',dataSetId);
        param.append('pageIndex',indexCorpus);
        param.append('pageSize',sizeCorpus);
        param.append('objectId',objectId);
        param.append('needText',`${needText==false?false:true}`);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/listDataSetCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            corpusData=data.corpus;
            rightList=[];
            corpusData.map((value)=>{
                var temp = {
                    text: `${ value.title }`,
                    key: `${ value._id+','+value.higherLevel+','+value.title+','+value.higherLevelId}`,
                };
                rightList.push( temp );
            });
            rightListAll=rightList;
            this.handleSelectFetch(corpusData);
            totalCorpus=data.totalNum;
            onePageTransferBool=false;
            this.setState({corpusData,totalCorpus,rightList,onePageTransferBool,rightListAll},()=>this.showObjects());
        }).catch((err) => console.log(err.toString())); 
    }
    //获取语料集
    fetchCorpusSet(value){
        let {functionsTypesId,corpusSetData}=this.state;
        functionsTypesId=value[1];
        let param = new FormData();
        param.append('functionId',functionsTypesId);
        param.append('pageIndex',1);
        param.append('pageSize',10000000);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/corpusSet/listCorpusSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let data1=data.corpusSet.filter((element) => {return element.corpusNum >0});
           corpusSetData=data1.map((value)=>{
               return {
                   text:value.name,
                   value:value.classifyId+","+value.id+","+value.corpusNum
               }
           });
           this.setState({corpusSetData});
        }).catch((err) => console.log(err.toString()));
    }
    //获取语料集下最大对象数量
    getCorpusMaxNum(data){
        let param = new FormData();
        param.append('classifyId',data.split(",")[0]);
        param.append('pageIndex',1);
        param.append('pageSize',100000);
        param.append('corpusSetId',data.split(",")[1]);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getClassifyObject', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let courpusNumArr=data.objects.map((value)=>{
                return Number(value.courpusNum);
            });
            let corpusMaxNum=courpusNumArr.sort(function (x, y) {
                                                if (x < y) {
                                                    return 1;
                                                }
                                                if (x > y) {
                                                    return -1;
                                                }
                                                return 0;
                                            })[0];
            this.setState({corpusMaxNum});
        }).catch((err) => console.log(err.toString())); 
    }
    //新建数据集
    closeAddDataSet(){
        let {corpusMaxNum,random,addDataSetVisible}=this.state;
        this.resetAdd();
        this.resetUpload();
        corpusMaxNum=-1;
        random=false;
        addDataSetVisible=false;
        this.setState({ addDataSetVisible,corpusMaxNum,random });
    }
    beforeAddDataSetSubmit(data){
        if(this.state.progressBool==false){
            data.name=this.trim(data.name);
            if(data.name==''){
                popup(<Snackbar message="请输入语料集名称！"/>) 
                return;
            }
            if(data.typeFunction.length==0){
                popup(<Snackbar message="请选择类型功能！"/>) 
                return;
            }
            let {random}=this.state;
            if(random){
                if(data.classifySet.length==0){
                    popup(<Snackbar message="请选择语料集！"/>) 
                    return;
                }
                if(data.corpusNum.length==0){
                    popup(<Snackbar message="随机语料数不能为空！"/>) 
                    return;
                }else if(parseInt(data.corpusNum).toString().length!==data.corpusNum.length){
                    popup(<Snackbar message="请输入正确格式的数字！"/>) 
                    return;
                }else if(parseInt(data.corpusNum)<=0){
                    popup(<Snackbar message="随机语料数必须大于零！"/>) 
                    return;
                }else if(parseInt(data.corpusNum)>parseInt(this.state.corpusMaxNum)){
                    popup(<Snackbar message="请输入不大于所选语料集最大语料数！"/>) 
                    return;
                }
                this.addDataSetSelect(data);
            }else{
                if(data.file.length==0){
                    popup(<Snackbar message="请选择上传的文件！"/>) 
                    return;
                }
                if(data.file.length>1){
                    popup(<Snackbar message="只能上传一个压缩文件！"/>) 
                    return;
                }
                this.setState({progressBool:true});
                this.addDataSetUpload(data);
            }
        }
    }
    afterAddDataSetSubmit(data){
    }
    //选择语料
    corpusTransfer(){
        let {corpusTransferVisible,onePageTransferBool}=this.state;
        corpusTransferVisible=true;
        onePageTransferBool=true;
        let leftList=[];
        let rightList=[];
        this.setState({corpusTransferVisible,onePageTransferBool,leftList,rightList},()=>this.fetchCorpusTransfer("id"));
    }
    closeSelectCorpus(){
        let {corpusTransferVisible,onePageTransferBool}=this.state;
        corpusTransferVisible=false;
        onePageTransferBool=false;
        this.clickResetTransfer();
        this.setState({corpusTransferVisible,onePageTransferBool},()=>this.fetchCorpus());
    }
    beforeTransferSubmit(data){
        let {dataSetId,corpusSetId,addCorpusIds,removeCorpusIds}=this.state;
        data={};
        data.dataSetId=dataSetId;
        data.corpusSetId=corpusSetId;
        data.addCorpusIds=addCorpusIds.map((value)=>{return value.key.split(',')[0]+':'+value.key.split(',')[3]});
        data.removeCorpusIds=removeCorpusIds.map((value)=>{return value.key.split(',')[0]+':'+value.key.split(',')[3]});
        return data;
    }
    afterTransferSubmit(data){
        data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.transferSuccess() : popup(<Snackbar
         message={data.msg}/>)
    }
    transferSuccess(){
        let {onePageTransferBool,corpusTransferVisible}=this.state;
        onePageTransferBool=false;
        corpusTransferVisible=false;
        let indexCorpus=1;
        let sizeCorpus=10;
        let pageStart=0;
        let searchTableBool=false;
        let addCorpusIds=[];
        let removeCorpusIds=[];
        this.resetSearchRule();
        this.clickResetTransfer();
        this.setState({onePageTransferBool,corpusTransferVisible,indexCorpus,sizeCorpus,pageStart,searchTableBool,addCorpusIds,removeCorpusIds},()=>{this.fetchDataSet();this.fetchObject();});
    }
    getCorpusTransfer(id){
        let {objectId,indexCorpus,size,onePageTransferBool,objectBool,objectsTransferData}=this.state;
        objectId=id;
        onePageTransferBool=true;
        if(objectsTransferData[0].objectId!==id){
            objectBool=true;
        }
        this.setState({objectId,indexCorpus,size,onePageTransferBool,objectBool},()=>this.fetchCorpusTransfer(id));
    }
    fetchCorpusTransfer(id){
        let needText=false;
        let {objectsTransferData,objectBool}=this.state;
        objectsTransferData[0].objectId==id||objectBool==false?this.fetchCorpusTransferCorpusSet()+this.fetchCorpusDataSet(needText):this.fetchCorpusTransferObject()+this.fetchCorpusObject(needText)
    }
     //获取语料集下剩余语料
    fetchCorpusTransferCorpusSet(){
        let {dataSetId,corpusSetId,objectId,leftList}=this.state;
        let param = new FormData();
        param.append('dataSetId',dataSetId);
        param.append('pageIndex',1);
        param.append('pageSize',100000);
        param.append('corpusSetId',corpusSetId);
        param.append('objectId',"");
        param.append('needText',false);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/corpusSet/listShuttleBoxObject', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            leftList=[];
            data.corpus.map((value)=>{
                var temp = {
                    text: `${ value.title }`,
                    key: `${ value._id+','+value.higherLevel+','+value.title+','+value.higherLevelId}`,
                    higherLevel:`${ value.higherLevel }`
                };
                leftList.push( temp );
            });
            this.setState({leftList});
        }).catch((err) => console.log(err.toString())); 
    }
    //获取语料集对象下剩余语料
    fetchCorpusTransferObject(){
        let {dataSetId,corpusSetId,objectId,leftList}=this.state;
        let param = new FormData();
        param.append('dataSetId',dataSetId);
        param.append('pageIndex',1);
        param.append('pageSize',10000000);
        param.append('corpusSetId',corpusSetId);
        param.append('objectId',objectId);
        param.append('needText',false);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/corpusSet/listShuttleBoxObject', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            leftList=[];
            data.corpus.map((value)=>{
                var temp = {
                    text: `${ value.title }`,
                    key: `${ value._id+','+value.higherLevel+','+value.title+','+value.higherLevelId}`,
                };
                leftList.push( temp );
            });
            this.setState({leftList});
        }).catch((err) => console.log(err.toString())); 
    }
    //创建数据集(选择)
    addDataSetSelect(data){
        let param = new FormData();
        param.append('name',data.name);
        param.append('corpusTypeId',data.typeFunction[0]);
        param.append('functionId',data.typeFunction[1]);
        param.append('classifyId',data.classifySet.split(",")[0]);
        param.append('corpusSetId',data.classifySet.split(",")[1]);
        param.append('random',this.state.random);
        param.append('corpusNum',data.corpusNum);

        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/addDataSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchDataSet()+this.creatSuccessSelect(): popup(<Snackbar
            message={data.msg}/>)
        }).catch((err) => console.log(err.toString()));
    }
    creatSuccessSelect(){
        let {indexDataSet,corpusMaxNum,random}=this.state;
        this.resetAdd();
        indexDataSet=1;
        corpusMaxNum=-1;
        random=false;
        this.setState({addDataSetVisible:false,indexDataSet,corpusMaxNum,random},()=>this.fetchDataSet());
    }
    //创建数据集(上传)
    addDataSetUpload(data){
        let {indexDataSet}=this.state;
        let param = new FormData();
        param.append('name',data.name);
        param.append('corpusTypeId',data.typeFunction[0]);
        param.append('functionId',data.typeFunction[1]);
        param.append('file',data.file[0]);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/dataSet/uploadDataSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchDataSet()+this.creatSuccessUpload(): popup(<Snackbar
            message={data.msg}/>)+this.uploadFail()
        }).catch((err) => console.log(err.toString()));
    }
    creatSuccessUpload(){
        let {indexDataSet,random}=this.state;
        this.resetAdd();
        this.resetUpload();
        indexDataSet=1;
        random=false;
        this.setState({addDataSetVisible:false,indexDataSet,random},()=>this.fetchDataSet());
    }
    uploadFail(){
        this.setState({progressBool: false});
    }
    //切换Tabs
    clickTab(index,title){
        let {random}=this.state;
        if(index==0){
            random=false;
        }else{
            random=true;
        }
        this.setState({random,corpusMaxNum:-1});
        this.resetUpload();
        this.resetNumber();
        this.resetCorpus();
    }
    //清空回显
    formTirggerUpload( trigger ) {
        this.resetUpload = trigger.reset;
    }
    formTirggerNumber(trigger){
        this.resetNumber = trigger.reset;
    }
    formTirggerCorpus(trigger){
        this.resetCorpus = trigger.reset;
    }
    formTirggerEdit(trigger){
        this.resetEdit = trigger.reset;
    }
    formTirggerTransfer(trigger){
        this.resetTransfer = trigger.reset;
    }
    searchTirggerRule(trigger){
        this.resetSearchRule = trigger.reset;
    }
    //点击reset
    clickResetTransfer(){
        this.resetTransfer();
        this.setState({descriptionTransfer:''});
    }
    //穿梭框
   /*  handleListChange( leftList, rightList, direction, moveList){
        let {rightListAll}=this.state;
        if(direction=="toRight"&&moveList.length>0){
            //console.log(rightListAll);
            moveList.map((value)=>{
                //console.log("WHY");
                //console.log(value);
                rightListAll.push(value);
            });
        }else if(direction=="toLeft"&&moveList.length>0){
            moveList.map((value1)=>{
                rightListAll.map((value2,index)=>{
                    if(value1.key==value2.key){
                        rightListAll.splice(index,1);
                    }
                });
            });
        }
        // console.log("穿梭狂rightListAll");
        // console.log(rightListAll);
        this.setState({rightList,rightListAll});
    } */
     handleListChange( leftList, rightList, direction, moveList){
        let {addCorpusIds,removeCorpusIds}=this.state;
        if(direction=="toRight"&&moveList.length>0){
            //console.log(rightListAll);
            moveList.map((value)=>{
                //console.log("WHY");
                //console.log(value);
                if(removeCorpusIds.length>0){
                    let qq=true;
                    removeCorpusIds.map((value2,index)=>{
                        if(value.key==value2.key){
                            removeCorpusIds.splice(index,1);
                            qq=false;
                        }
                    });
                    if(qq){
                        addCorpusIds.push(value);
                    }
                }else{
                    addCorpusIds.push(value);
                }
            });
        }else if(direction=="toLeft"&&moveList.length>0){
            moveList.map((value)=>{
                if(addCorpusIds.length>0){
                    let qq=true;
                    addCorpusIds.map((value2,index)=>{
                        if(value.key==value2.key){
                            addCorpusIds.splice(index,1);
                            qq=false;
                        }
                    });
                if(qq){
                    removeCorpusIds.push(value);
                    }
                }else{
                    removeCorpusIds.push(value);
                }
            });
        }
        addCorpusIds=[...new Set(addCorpusIds)];
        removeCorpusIds=[...new Set(removeCorpusIds)];
        // console.log("穿梭狂rightListAll");
        // console.log(rightListAll);
        this.setState({addCorpusIds,removeCorpusIds});
    }
    handleListCheck(listName,checkedData){
        //let checkedDataId=[];
        // checkedData.map((value,index)=>{
        //     checkedDataId.push(value.split(',');
        // });
        let {leftListCheck,rightListCheck}=this.state;
        let viewCorpus='';
        if(listName=="leftList"){
            if(checkedData.length>leftListCheck.length){
                viewCorpus=checkedData[checkedData.length-1];
            }else{
                leftListCheck.map((value,index)=>{
                    if(checkedData.indexOf(value)==-1){
                        viewCorpus=leftListCheck[index];
                    }
                })
            }
            leftListCheck=[];
            checkedData.map((value)=>{
                leftListCheck.push(value);
            });
        }else{
             if(checkedData.length>rightListCheck.length){
                viewCorpus=checkedData[checkedData.length-1];
            }else{
                rightListCheck.map((value,index)=>{
                    if(checkedData.indexOf(value)==-1){
                        viewCorpus=rightListCheck[index];
                    }
                })
            }
            rightListCheck=[];
            checkedData.map((value)=>{
                rightListCheck.push(value);
            });
        }
        this.viewCorpus1(viewCorpus);
        this.setState({leftListCheck,rightListCheck});
    }
    render(){
        let {bodyHtml,editShowCorpusSet,addDataSetVisible,typeFunction,corpusSetData,corpusMaxNum,corpusTransferVisible,leftList,rightList,editDataSetVisible,editShowDataSet,objectsTransferData,objectId,objectBool,descriptionTransfer,higherLevelTransfer,titleTransfer,progressBool} = this.state;
        const listStyle = {
            width: '220px',
            height: '320px'
        };
        return(
            <Page>
                {/*<COMM_HeadBanner prefix=" " />
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px',margin:'0px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'910px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <NLPMenu url={'/nlap/platform/dataSet'}/>
                    </Col>*/}
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col">
                                <div className = "corpusManage_title" >
                                    <span className = "corpusManage_span">数据集管理</span>
                                </div>
                            </Col>
                            {bodyHtml}
                        </Row>
                    </Col>
                </Row>
                <Form method="post"
                        async={true}
                        type="horizontal"
                        enctype="multipart/form-data"
                        onSubmit={this.beforeAddDataSetSubmit.bind(this)}
                        onAfterSubmit={this.afterAddDataSetSubmit.bind(this)}
                        trigger={ ( trigger ) => {
                            this.resetAdd = trigger.reset;
                        } }
                >
                    {
                        addDataSetVisible?
                        <Modal visible={ addDataSetVisible } size="medium" onClose={ this.closeAddDataSet.bind( this ) }>
                            <ModalHeader>
                                新建数据集
                            </ModalHeader>
                            <ModalBody>
                                <Col size={{ normal: 12, small: 24, medium: 24, large: 24 }}>
                                    <FormItem>
                                        <Label>数据集名称</Label>
                                        <Input name="name" placeholder="请输入数据集名称" />                                    
                                    </FormItem>
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 24, large: 24 }}>
                                    <FormItem>
                                        <Label>功能</Label>
                                        <Cascade name="typeFunction" dataSource={typeFunction} dataValueMapper="value" onChange={this.fetchCorpusSet.bind(this)} placeholder="类型/功能"/>
                                    </FormItem>
                                </Col>
                                <Divider line />
                                <Col size={{ normal: 12, small: 24, medium: 24, large: 24 }}>
                                    <Tabs onClick={this.clickTab.bind(this)}>
                                        <Tab title="上传">
                                            <FormItem>
                                                <Label>上传文件</Label>
                                                <Upload placeholder="选择文件" name="file" multiple  trigger={ this.formTirggerUpload.bind(this) }/>
                                            </FormItem>
                                            {
                                                progressBool?
                                                <Progress type="loading" />
                                                :
                                                null
                                            }
                                            <span className="upExamples"> 系统支持单个.zip,.rar格式的文本文件进行上传。</span>
                                        </Tab>
                                        <Tab title="选择">
                                            <FormItem>
                                                <Label>语料集</Label>
                                                <Select name="classifySet" placeholder="请选择语料集" dataSource={corpusSetData} onChange={this.getCorpusMaxNum.bind(this)} showClear={ false } trigger={ this.formTirggerCorpus.bind(this)}/>
                                            </FormItem>
                                            <FormItem>
                                                <Label>随机</Label>
                                                <Input name="corpusNum" placeholder={`${corpusMaxNum==-1?"请选择语料数量":"请输入>0&&<="+`${corpusMaxNum}`+"的数字"}`} trigger={ this.formTirggerNumber.bind(this) }/>
                                            </FormItem>
                                        </Tab>
                                    </Tabs>
                                </Col>
                            </ModalBody>
                            <ModalFooter>
                                <Button onClick={this.closeAddDataSet.bind(this)}>关闭</Button>
                                <Button type="primary" htmlType="submit">确定</Button>
                            </ModalFooter>
                        </Modal>
                        :
                        null
                    }
                </Form>
                <Form method="post"
                        action={contextUrl + '/nlap/admin/corpusMgmt/dataSet/operateShuttle'}
                        async={true}
                        type="horizontal"
                        enctype="multipart/form-data"
                        onSubmit={this.beforeTransferSubmit.bind(this)}
                        onAfterSubmit={this.afterTransferSubmit.bind(this)}
                >
                    <Modal visible={ corpusTransferVisible } size="large" onClose={ this.closeSelectCorpus.bind( this ) }>
                        <ModalHeader>
                            选择语料
                        </ModalHeader>
                        <ModalBody>
                            <Row>
                                <Col size={{ normal: 14, small: 14, medium: 14, large: 14 }}>
                                    {
                                        <Select placeholder="请选择语料对象" search={ true } disabled onChange={this.getCorpusTransfer.bind(this)} value={`${objectBool==false&&objectsTransferData.length>0?objectsTransferData[0].objectId:objectId}`} style={{width:'500px',marginBottom:'16px'}} showClear={ false }>
                                            {
                                                Object.keys(objectsTransferData).map((index)=>{ 
                                                    return(
                                                        <Option value={objectsTransferData[index].objectId} key={index}>{objectsTransferData[index].objectName} </Option>
                                                    )
                                                })
                                            }
                                        </Select>
                                    }
                                    <Transfer
                                        leftList={ leftList }
                                        rightList={ rightList }
                                        titles={ [ '未选语料', '已选语料' ] }
                                        render={ item => `${ item.text }` }
                                        listStyle={ listStyle }
                                        onChange={ this.handleListChange.bind(this) }
                                        onCheck={ this.handleListCheck.bind(this) }
                                        trigger={ this.formTirggerTransfer.bind( this ) }
                                        />  
                                    <Button className="resetButton" onClick={ this.clickResetTransfer.bind( this ) }>重置</Button>
                                </Col>
                                <Col size={{ normal: 10, small: 10, medium: 10, large: 10 }}>
                                    <p style={{height:'32px',marginBottom:'16px',lineHeight:'32px'}}>所属对象：{`${descriptionTransfer==''?'':higherLevelTransfer}`}</p>
                                    <p style={{height:'32px',marginBottom:'16px',lineHeight:'32px'}}>文件名称：{`${descriptionTransfer==''?'':titleTransfer}`}</p>
                                    {
                                        descriptionTransfer==''?
                                        <div className="detailsNone">
                                            点击语料查看详情
                                        </div>
                                        :
                                        <div className="details">
                                            {descriptionTransfer}
                                        </div>
                                    }
                                </Col>
                            </Row>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeSelectCorpus.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/corpusMgmt/dataSet/updateDataSet'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeEditDataSetSubmit.bind(this)}
                          onAfterSubmit={this.afterEditDataSetSubmit.bind(this)}
                          trigger={ this.formTirggerEdit.bind(this) }
                    >
                    <Modal visible={ editDataSetVisible } size="middle" onClose={ this.closeEditDataSet.bind(this) }>
                        <ModalHeader>
                            编辑数据集
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>数据集名称</Label>
                                <Input name="name" placeholder="请输入数据集集名称" value={editShowDataSet.dataSetName}/>
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeEditDataSet.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                {/*<Footer />*/}
            </Page>
        );
    };
}

DataSet.UIPage = page;
export default DataSet;