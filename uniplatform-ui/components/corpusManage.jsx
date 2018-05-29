import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,DateTime,Dialog,Notification,Progress,Dropdown} from 'epm-ui';
/*import { Footer,COMM_HeadBanner,NLPMenu} from '../../../components/uniplatform-ui';*/

const contextUrl = '/uniplatform';

const page = {
    title: '语料管理',
    css: [
        '../css/index.min.css',
        '../css/corpusManage.min.css',
        '../css/leftnav.min.css'
    ]
};

class CorpusManage extends Component{
     constructor(props){
        super(props);

        this.state = {
           corpusTypes:[],
           corpusTypesIndex:0,
           corpusTypeId:'',
           functionsTypes:[],
           functionsTypeIndex:0,
           functionsTypesId:'',
           bodyHtml:[],
           classifyId:"",
           corpusSetId:"",
           corpusSetData:[],
           corpusSet_index:"",
           objectsData:[],
           objectId:"",
           colorIndex:'Classify',
           size:10,
           sizeCorpusSet:9,
           indexCorpusSet:1,
           totalCorpusSet:0,
           indexObject:1,
           totalObject:0,
           sizeCorpus:10,
           indexCorpus:1,
           totalCorpus:0,
           corpusData:[],
           pageStart:0,
           objectBool:false,
           showCorpusSetBool:true,
           viewCorpusVisible:false,
           selectData:[],
           selectDataId:[],
           selectAllDataId:[],
           addCorpusSetVisible:false,
           editCorpusSetVisible:false,
           classifyData:[],
           editShowCorpusSet:"",
           resultBool:true,
           resultSetBool:true,
           pageBool:false,
           progressBool:false,
           isUsedCorpusSet:false,
           objectsDataAll:[],
           searchTableBool:false
        }
       this.formTirggerUp=this.formTirggerUp.bind(this);
       this.formTirggerAdd=this.formTirggerAdd.bind(this);
       this.formTirggerEdit=this.formTirggerEdit.bind(this);
    }
    componentDidMount() {
       this.fetchCorpusTypes();
    }
    //获取语料类型
    fetchCorpusTypes(){
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getCorpusType', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            let corpusTypes=data.corpusTypes;
            this.setState({corpusTypes},()=>this.clickCorpusTypes(3));
        }).catch((err) => console.log(err.toString()));
    }
    //获取分类体系
    fetchClassify(){
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/listClassifyName', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            let classifyData=data.classify;
            this.setState({classifyData});
        }).catch((err) => console.log(err.toString()));
    }
    //点击语料类型
    clickCorpusTypes(index){
        let {corpusTypesIndex,corpusTypes,corpusTypeId,indexCorpusSet,indexObject,colorIndex,indexCorpus,showCorpusSetBool,searchTableBool}=this.state;
        corpusTypesIndex=index;
        corpusTypeId=corpusTypes[index].id;
        indexCorpusSet=1;
        indexObject=1;
        colorIndex='Classify';
        indexCorpus=1;
        showCorpusSetBool=true;
        Object.keys(corpusTypes).map((index) => 
           corpusTypes[index].status=false
        );
        corpusTypes[index].status=true;
        searchTableBool=false;
        this.setState({corpusTypes,corpusTypeId,corpusTypesIndex,indexCorpusSet,indexObject,colorIndex,indexCorpus,showCorpusSetBool,searchTableBool},()=>this.getFunctions());
    }
    //获取功能
    getFunctions(){
        let {corpusTypesIndex,corpusTypes,bodyHtml,functionsTypes,functionsTypesId,functionsTypeIndex,sizeCorpusSet,resultSetBool,searchWordSet}=this.state;
        let param = new FormData();
        param.append('corpusTypeId',this.state.corpusTypeId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getFunctions', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let bodyHtml=[];
            switch(corpusTypesIndex){
                case 0:
                    bodyHtml.push(corpusTypes[0].name);
                    break;
                case 1:
                    bodyHtml.push(corpusTypes[1].name);
                    break;
                case 2:
                    functionsTypes=data.functions;
                    functionsTypesId=functionsTypes[0].id;
                    functionsTypeIndex=0;
                    this.setState({functionsTypes,functionsTypesId,functionsTypeIndex},()=>this.fetchCorpusSet());
                    break;
                case 3:
                    functionsTypes=data.functions;
                    functionsTypesId=functionsTypes[0].id;
                    functionsTypeIndex=0;
                    if(this.state.firstIn==false){
                        searchWordSet='';
                        sizeCorpusSet=9;
                        resultSetBool=true;
                    }
                    this.setState({functionsTypes,functionsTypesId,functionsTypeIndex,sizeCorpusSet,resultSetBool,searchWordSet},()=>this.fetchCorpusSet());
                    break;
                    default:
                bodyHtml.push("default");
                break;

           };
           this.setState({bodyHtml});
        }).catch((err) => console.log(err.toString()));
    }
    //过滤分词语料功能
    clickFunctionsTypes(index){
        let {functionsTypeIndex,functionsTypesId,indexCorpusSet,functionsTypes,firstIn,sizeCorpusSet,resultSetBool,searchWordSet}=this.state;
        functionsTypeIndex=index;
        functionsTypesId=this.state.functionsTypes[index].id;
        indexCorpusSet=1;
        Object.keys(functionsTypes).map((index) => 
           functionsTypes[index].status=false
        );
        functionsTypes[index].status=true;
        if(firstIn==false){
            searchWordSet='';
            sizeCorpusSet=9;
            resultSetBool=true;
        }
        firstIn=false;
        this.setState({functionsTypesId,functionsTypeIndex,indexCorpusSet,functionsTypes,sizeCorpusSet,resultSetBool,firstIn,sizeCorpusSet,resultSetBool,searchWordSet},()=>this.fetchCorpusSet());
    }
    //点击分语料集
    clickCorpusSet(index){
        let {corpusSet_index,corpusSetData,showCorpusSetBool,classifyId,corpusSetId,isUsedCorpusSet}=this.state;
        corpusSet_index=index;
        $(document).scrollTop(300);
        isUsedCorpusSet=corpusSetData[corpusSet_index].isUsed;
        classifyId=corpusSetData[corpusSet_index].classifyId;
        corpusSetId=corpusSetData[corpusSet_index].id;
        showCorpusSetBool=false;
        this.setState({classifyId,corpusSet_index,showCorpusSetBool,corpusSetId,isUsedCorpusSet},()=>{this.fetchObject();this.fetchCorpusClassify()});
    }
    //获取语料集下对象
     fetchObject(){
        let {indexObject,totalObject,size,classifyId,corpusSetId,colorIndex,objectId,objectsData,pageBool}=this.state;
        let param = new FormData();
        param.append('classifyId',classifyId);
        param.append('pageIndex',indexObject);
        param.append('pageSize',size);
        param.append('corpusSetId',corpusSetId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getClassifyObject', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            totalObject=data.totalNumber;
            let objectsData=data.objects;
            if(colorIndex!=="Classify"&&pageBool==true){
                colorIndex=0;
                objectId=objectsData[0].id;
            }
            //this.setState({objectsData,totalObject,colorIndex,objectId,objectsData,pageBool},()=>this.fetchCorpus());
            this.setState({objectsData,totalObject,colorIndex,objectId,objectsData,pageBool},()=>{this.state.searchTableBool?this.searchCorpus():this.fetchCorpus()});
        }).catch((err) => console.log(err.toString())); 
    }
    //展示对象
    showObjects(){
        let {objectsData,colorIndex,indexObject,totalObject,size,sizeCorpus,corpusData,indexCorpus,totalCorpus,objectBool,corpusSetData,corpusSet_index,resultBool,isUsedCorpusSet}=this.state;
        let bodyHtml=[];
        bodyHtml.push(
            <div key="">
                <div className="corpusManage_content">
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                        <Button type="default" size="medium" style={{float:'right'}} onClick={this.downCorpusSet.bind(this,corpusSetData[corpusSet_index])} disabled={eval(`${corpusSetData[corpusSet_index].corpusNum==0||(colorIndex=='Classify'?false:objectsData[colorIndex].courpusNum== 0)}`)}>
                            <i className="fa fa-download"></i> 导出语料
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
                                        <span className = "corpusManage_span" style={{height:'35px',textAlign:'center'}} >{corpusSetData[corpusSet_index].name}</span>
                                    </Dropdown.Trigger>
                                    <Dropdown.Content>
                                        <div style={ { width: '200px', maxHeight:'200px',overflow:'auto' } }>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>语料名称：{corpusSetData[corpusSet_index].name}</p>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>体系：{corpusSetData[corpusSet_index].classifyName}</p>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>语量：{corpusSetData[corpusSet_index].corpusNum}</p>
                                            <p style={{marginLeft:'20px',marginRight:'20px'}}>状态：{corpusSetData[corpusSet_index].isUsed==true?"已使用":"未使用"}</p>
                                        </div>
                                    </Dropdown.Content>
                                </Dropdown>
                            </div>
                        </Col>
                        <div className="corpusManage_content hang" onClick={this.clickInClassify.bind(this)} style={{backgroundColor:`${colorIndex==="Classify"?"rgb(206, 238, 252)":""}`,border:`${colorIndex==="Classify"?"1px solid rgb(111, 204, 245)":""}`}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cent" style={{height:'156px',zIndex:'1000',cursor:'pointer'}}>
                                <p>
                                    <span style={{fontSize:'14px'}}>体系：</span>
                                    <span style={{fontSize:'16px',color:'#b67561'}}>{corpusSetData[corpusSet_index].classifyName}</span>
                                </p>
                                <p>
                                    <span style={{fontSize:'14px'}}>语量：</span>
                                    <span style={{fontSize:'16px',color:'#b67561'}}>{corpusSetData[corpusSet_index].corpusNum}</span>
                                </p>
                                <p>
                                    <span style={{fontSize:'14px'}}>状态：</span>
                                    <span style={{fontSize:'16px',color:'#b67561'}}>{corpusSetData[corpusSet_index].isUsed==true?"已使用":"未使用"}</span>
                                </p>
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
                                                        <span className = "corpusManage_span" style={{height:'35px',textAlign:'center'}} >{value.name}</span>
                                                    </div>
                                                </Col>
                                                <div className="corpusManage_content hang" onClick={this.clickObject.bind(this,index)} style={{backgroundColor:`${colorIndex===index?"rgb(206, 238, 252)":""}`,border:`${colorIndex===index?"1px solid rgb(111, 204, 245)":""}`}}>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="cent" style={{height:'156px',zIndex:'1000',cursor:'pointer'}}>
                                                        <p>
                                                            <span style={{fontSize:'14px'}}>数量：</span>
                                                            <span style={{fontSize:'16px',color:'#b67561'}}>{value.courpusNum}</span>
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
                        <span className="nullContent">该体系不存在对象</span>
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
                                trigger={ this.searchTirggerCorpus.bind(this) }>
                                <Input.Left icon="search" />
                            </Input>
                            <Button type="default" size="medium" style={{float:'right'}} disabled={eval(`${corpusData.length==0||isUsedCorpusSet}`)} onClick={this.delSelectData.bind(this)} >
                                <i className="fa fa-trash"></i> 批量删除
                            </Button>
                            <Button type="default" size="medium" style={{float:'right'}} disabled={eval(`${corpusData.length==0}`)} onClick={this.getJsonByIdDown.bind(this)}>
                                <i className="fa fa-download"></i> 导出语料
                            </Button>
                            <Button type="default" size="medium" style={{float:'right'}} onClick={this.uploadCorpusModal.bind(this)}>
                                <i className="fa fa-upload"></i> 上传语料
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
                                            <Column title="语料内容" scaleWidth = '33%'textAlign="center">
                                                { ( value, index ) => { return <p style={{overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap',cursor:'pointer'}} onClick={this.viewCorpus.bind(this,value,index)}>{value.text}</p> }}
                                            </Column>
                                            <Column title="更新时间"  scaleWidth = '20%'textAlign="center">
                                                { ( value, index ) => { return <DateTime format='yyyy/MM/dd hh:mm:ss' content={value.update_time} style={{textAlign:'center'}}/> } }
                                            </Column>
                                            <Column title="操作" scaleWidth = '20%' textAlign="center">
                                                { ( value ) => {
                                                    return (
                                                        <div>
                                                            {
                                                                isUsedCorpusSet?
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
                                        <Pagination index={ indexCorpus }  total={totalCorpus} size={ sizeCorpus } align='center' onChange={this.exchangePageCorpus.bind(this)} showDataSizePicker dataSizePickerList={['10','50','100','200']} />
                                    </Row>
                                    <Divider />
                                </div>
                            </Col>
                            :
                            resultBool==true?
                                objectBool?
                                <span className="nullContent">该对象不存在语料</span>
                                :
                                <span className="nullContent">该体系不存在语料</span>
                            :
                            <span className="nullContent">不存在对应的语料</span>
                        }
                    </div>
                </div>
            </div>
        );
        this.setState({bodyHtml});
    }
    //通过关键字检索语料集
    getWordInputSet(getter){
        this.getWordValueSet=getter.value;
    }
    wordSearchSet(event){
        let {resultSetBool}=this.state;
        let indexCorpusSet=1;
        if (event.which==13){
            let searchWordSet = this.getWordValueSet();
            if(searchWordSet==''){
                resultSetBool=true;
            }else{
                resultSetBool=false;
            }
            this.setState({searchWordSet,indexCorpusSet,resultSetBool}, () => this.searchCorpusSet());
        }
    }
    wordSearchAutoSet(data){
        let indexCorpusSet=1;
        if(data==""){
            let resultSetBool=true;
            this.setState({searchWordSet:"",resultSetBool,indexCorpusSet}, () => this.searchCorpusSet());
        }
    }
    searchCorpusSet(){
        let {size,sizeCorpusSet,indexCorpusSet,searchWordSet,functionsTypesId,resultSetBool}=this.state;
        if(searchWordSet==''){
            sizeCorpusSet=9;
        }else{
            sizeCorpusSet=size;
        }
        let param = new FormData();
        param.append('pageSize',`${searchWordSet==''?sizeCorpusSet:size}`);
        param.append('pageIndex',indexCorpusSet);
        param.append('keyWord',searchWordSet);
        param.append('functionId',functionsTypesId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/corpusSet/searchCorpusSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let corpusSetData=data.corpus;
            let totalCorpusSet=data.totalNum;
            this.setState({corpusSetData,totalCorpusSet,sizeCorpusSet},()=>this.showCorpusSet());
        }).catch((err) => console.log(err.toString()));
    }
    //通过关键字检索语料
    getWordInput(getter){
        this.getWordValue=getter.value;
    }
    wordSearch( event ) {
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
            let pageStart=0;
            let indexCorpus=1;
            let sizeCorpus=10;
            this.setState({searchWord:"",pageStart,indexCorpus,sizeCorpus}, () => this.searchCorpus());
        }
    }
    searchCorpus(){
        let {size,indexCorpus,searchWord,corpusSetId,objectId,corpusData,totalCorpus,resultBool}=this.state;
        let param = new FormData();
        param.append('pageSize',size);
        param.append('pageIndex',indexCorpus);
        param.append('keyWord',searchWord);
        param.append('corpusSetId',corpusSetId);
        param.append('objectId',objectId);
        param.append('needText',true);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/searchCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            corpusData=data.corpus;
            totalCorpus=data.totalNum;
            resultBool=false;
            this.setState({ corpusData,totalCorpus,resultBool},()=>this.showObjects());
        }).catch((err) => console.log(err.toString()));
    }
    //选择表格数据
    handleSelect(data,currentData ){
        let {selectDataId,selectData,selectAllDataId}=this.state;
        if(selectAllDataId.length==data.length){
            data.map((value)=>{
                selectDataId.push(value.higherLevelId+":"+value._id);
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
                if(selectDataId.indexOf(currentData.higherLevelId+":"+currentData._id)!==-1){
                    let index=selectDataId.indexOf(currentData.higherLevelId+":"+currentData._id);
                    selectDataId.splice(index,1);
                }else{
                    selectDataId.push(currentData.higherLevelId+":"+currentData._id);
                }
            }
        }
        selectData = selectDataId.join(';');
        this.setState({selectData,selectDataId});
    }
    handleSelectFetch(corpusData){
        let {selectDataId,selectAllDataId}=this.state;
        selectAllDataId=[];
        if(selectDataId){
            selectDataId.map((value1)=>{
                corpusData.map((value2)=>{
                    if(value1==(value2.higherLevelId+":"+value2._id)){
                        value2.checked=true;
                    }
                });
            });
        };
        if(corpusData){
            corpusData.map((value)=>{
                selectAllDataId.push(value.higherLevelId+":"+value._id);
            });
        };
        this.setState({selectAllDataId});
    }
    //导出语料
    getJsonByIdDown(){
        let {selectData,corpusSetId,corpusData} = this.state;
        if(selectData == ""){
            popup( <Snackbar message={`没有选择要导出的语料`} /> );
        }else{
            this.clearSelectData();
            window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/classify/exportToZip?higherLevelAndCorpusIds=${selectData}&corpusSetId=${corpusSetId}`,'_self');
        } 
    }
    getJsonOneDown(value){
        window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/corpusSet/exportOneCorpus?corpusId=${value._id}`,'_self');
    }
    //清空表格选中数据及状态
    clearSelectData(){
        let {corpusData} = this.state;
        corpusData.map((value)=>{
            value.checked=false;
        });
        this.setState({selectData:'',selectDataId:[],corpusData},()=>this.showObjects());
    }
    //删除语料
    delSelectData(){
        let {selectData,totalCorpus,size,indexCorpus}=this.state;
        if(selectData == ""){
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
        let {selectData,totalCorpus,size,indexCorpus}=this.state;
        let param = new FormData();
        param.append("higherLevelAndCorpusIds", selectData);
        fetch( Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/deleteCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body : param
            })
            .then((res) => res.json())
            .then((data) => {
                let index11=Math.ceil((totalCorpus-selectData.length)/size);
                if(index11<indexCorpus){
                    this.setState({indexCorpus:1,pageStart:0});
                }else{
                    this.setState({indexCorpus});
                }
                this.setState({selectData:'',selectDataId:[]});
                // data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchObject()+this.fetchCorpusSet()+this.fetchCorpus(): popup(<Snackbar
                // message={data.msg}/>)
                data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchObject()+this.fetchCorpusSet()+this.searchCorpus(): popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
            });
            after( true );
    }
    deleteCorpusOne(value){
        let selectData=value.higherLevelId+":"+value._id;
        this.setState({selectData});
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
    //确认删除单条语料
    handleCorpusOneApprove(after){
        let {selectData,totalCorpus,size,indexCorpus}=this.state;
        let param = new FormData();
            param.append('higherLevelAndCorpusIds',selectData);
         fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/deleteCorpus', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                if(totalCorpus%size==1&&indexCorpus==Math.ceil(totalCorpus/size) ){
                    this.setState({indexCorpus:1,pageStart:0});
                }
                // data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchObject()+this.fetchCorpusSet()+this.fetchCorpus() : popup(<Snackbar
                // message={data.msg}/>)
                data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchObject()+this.fetchCorpusSet()+this.searchCorpus() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
         after( true );
    }
    //查看语料
    viewCorpus(value,index){
        this.fetchObjectAll();
        this.setState({corpusContent:value},()=>{this.setState({viewCorpusVisible:true})});
    }
    closeNotification(){
        document.getElementsByClassName('notification')[0].style.visibility="hidden";
    }
    //点击对象
    clickObject(index){
        let {indexCorpus,pageStart,objectBool,objectId,colorIndex,objectsData,pageBool,sizeCorpus,searchTableBool}=this.state;
        indexCorpus=1;
        pageStart=0;
        objectBool=true;
        colorIndex=index;
        objectId=objectsData[index].id;
        pageBool=false;
        sizeCorpus=10;
        searchTableBool=false;
        this.resetSearchCorpus();
        this.setState({indexCorpus,pageStart,objectBool,colorIndex,objectId,pageBool,sizeCorpus,searchTableBool},()=>{this.fetchObject();this.fetchCorpusSet()});
    }
    //点击对象页面体系
    clickInClassify(){
        let {colorIndex,indexCorpus,pageStart,objectBool,objectId,sizeCorpus,searchTableBool}=this.state;
        colorIndex='Classify';
        indexCorpus=1;
        pageStart=0;
        objectBool=false;
        objectId="";
        sizeCorpus=10;
        searchTableBool=false;
        this.resetSearchCorpus();
        this.setState({colorIndex,indexCorpus,pageStart,objectBool,objectId,sizeCorpus,searchTableBool},()=>{this.fetchObject();this.fetchCorpusSet()});
    }
    //获取文本分类（语料集）
     fetchCorpusSet(){
        let {functionsTypesId,sizeCorpusSet,indexCorpusSet,totalCorpusSet,showCorpusSetBool,resultSetBool}=this.state;
        let param = new FormData();
        param.append('functionId',functionsTypesId);
        param.append('pageIndex',indexCorpusSet);
        param.append('pageSize',sizeCorpusSet);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/corpusSet/listCorpusSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            totalCorpusSet=data.totalNum;
            let corpusSetData=data.corpusSet;
            resultSetBool=true;
            this.setState({totalCorpusSet,corpusSetData,resultSetBool},()=>{if(showCorpusSetBool){this.showCorpusSet()}});
        }).catch((err) => console.log(err.toString()));
    }
    //展示分类体系内容
    showCorpusSet(){
         let {functionsTypeIndex,functionsTypes,sizeCorpusSet,indexCorpusSet,totalCorpusSet,bodyHtml,corpusSetData,resultSetBool,searchWordSet}=this.state;
         bodyHtml=[];
         switch(functionsTypeIndex){
                case 0 :
                    bodyHtml.push(
                        <div key="">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="filter">
                                <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</Button> 
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
                                        corpusSetData.length>0?
                                        corpusSetData.map((value,index)=>{
                                            return(
                                                <Col style={{width:'20%'}} key={index}>
                                                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col" style={{background:'#98d37b'}}>
                                                        <div className = "corpusManage_title">
                                                            <Dropdown position={'bottom'}>
                                                                <Dropdown.Trigger action="hover">
                                                                    <span className = "corpusManage_span" style={{height:'35px',textAlign:'center'}} >{value.name}</span>
                                                                </Dropdown.Trigger>
                                                                <Dropdown.Content>
                                                                    <div style={ { width: '200px', maxHeight:'200px',overflow:'auto' } }>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>语料名称：{value.name}</p>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>体系：{value.classifyName}</p>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>语量：{value.corpusNum}</p>
                                                                        <p style={{marginLeft:'20px',marginRight:'20px'}}>状态：{value.isUsed==true?"已使用":"未使用"}</p>
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
                                                                <span style={{fontSize:'16px',color:'#b67561'}}>{value.isUsed==true?"已使用":"未使用"}</span>
                                                            </p>
                                                            <div className="layer"></div>
                                                            {
                                                                value.isUsed==false?
                                                                <div className="layer_but">
                                                                    <a style={{backgroundColor: 'transparent', margin: '2%'}} onClick={this.delCorpusSet.bind(this,value)}>
                                                                        <img src="../image/del.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%',cursor:`${value.corpusNum==0?'not-allowed':''}`,opacity:`${value.corpusNum==0?'0.7':'1'}`}} onClick={this.downCorpusSet.bind(this,value)}>
                                                                        <img src="../image/down.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%'}} onClick={this.editCorpusSet.bind(this,value)}>
                                                                        <img src="../image/edit.png" />
                                                                    </a>
                                                                    <Divider />
                                                                    <Button type="info" block style={{backgroundColor:'#6fccf5',color:'white'}} onClick={this.clickCorpusSet.bind(this,index)}>
                                                                        详情
                                                                    </Button>
                                                                </div>
                                                                :
                                                                <div className="layer_but">
                                                                    <a style={{backgroundColor: 'transparent', margin: '2%',cursor: 'not-allowed',opacity:'0.7'}}>
                                                                        <img src="../image/del.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%'}} onClick={this.downCorpusSet.bind(this,value)}>
                                                                        <img src="../image/down.png"/>
                                                                    </a>
                                                                    <a style={{backgroundColor:'transparent',margin:'2%',cursor: 'not-allowed',opacity:'0.7'}}>
                                                                        <img src="../image/edit.png" />
                                                                    </a>
                                                                    <Divider />
                                                                    <Button type="info" block style={{backgroundColor:'#6fccf5',color:'white'}} onClick={this.clickCorpusSet.bind(this,index)}>
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
                                        ''
                                    }
                                    {
                                        resultSetBool?
                                        <Col style={{width:'20%'}} key="">
                                            <div style={{height:'200px',border:'dashed 1px #d8dde8',borderRadius:'20px',textAlign:'center'}}>
                                                <a type="default" size="tiny"
                                                style={{margin: '10px auto', position: 'relative', zIndex: 1}}
                                                onClick={this.showAddCorpusSetModal.bind(this)}
                                                >
                                                    <Icon icon="plus" className="plus fa-5x" style={{margin:'28% 0px',height:'74px',width:'74px',color:'#d8dde8'}}/>
                                                </a>
                                            </div>
                                        </Col>
                                        :
                                        corpusSetData.length>0?
                                            ""
                                            :
                                            <span className="nullContent">不存在对应的语料集</span>
                                    }
                                </Row>
                                <Row>
                                    <Pagination index={indexCorpusSet}  total={totalCorpusSet} size={sizeCorpusSet} align='center' onChange={this.exchangePageClassify.bind(this)}/>
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
                                <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</Button> 
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
                                <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</Button> 
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
                                <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</Button> 
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
                                <span className="filter_span"><label className="filter_label">功能：</label></span>
                                {
                                    functionsTypes.map((value,index)=>{ 
                                        return(
                                            <Button type="default" className="button_noborder" key={index} style={{backgroundColor:`${functionsTypes[index].status==true?"#eef1f6":""}`}} onClick={this.clickFunctionsTypes.bind(this,index)}>{value.displayName}</Button> 
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
    //返回
    quitObjectShow(){
        let {colorIndex,indexObject,indexCorpus,objectBool,showCorpusSetBool,sizeCorpus,searchTableBool}=this.state;
        indexObject=1;
        colorIndex='Classify';
        indexCorpus=1;
        objectBool=false;
        showCorpusSetBool=true;
        sizeCorpus=10;
        searchTableBool=false;
        this.setState({colorIndex,indexObject,indexCorpus,objectBool,showCorpusSetBool,sizeCorpus,searchTableBool},()=>this.fetchCorpusSetPage());
        $(document).scrollTop(0);
    }
     //获取语料集下全部对象
     fetchObjectAll(){
        let {classifyId,corpusSetId}=this.state;
        let param = new FormData();
        param.append('classifyId',classifyId);
        param.append('pageIndex',1);
        param.append('pageSize',10000);
        param.append('corpusSetId',corpusSetId);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/getClassifyObject', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            let objectsDataAll=data.objects;
            this.setState({objectsDataAll});
        }).catch((err) => console.log(err.toString())); 
    }
    //上传语料
    uploadCorpusModal(){
        this.fetchObjectAll();
        this.setState({ uploadCorpusVisible: true,progressBool:false});
    }
    closeUploadCorpus(){
        this.resetUp();
        this.setState({ uploadCorpusVisible: false });
    }
    beforeUploadSubmit(data){
        if(this.state.progressBool==false){
            data.corpusSetId=this.state.corpusSetId;
            if(data.files.length==0){
                popup(<Snackbar message="请选择需要上传的文件！"/>) 
                return;
            }
            let upFilesName=data.files.map((value)=>{
                return value.name.substring(value.name.length-4,value.name.length);
            });
            if([...new Set(upFilesName)].length>1){
                popup(<Snackbar message="一次只允许上传一种格式的文件！"/>) 
                return;
            }
            if(upFilesName.indexOf('.zip')!==-1&&upFilesName.length!==1){
                popup(<Snackbar message="一次只允许上传一个.zip格式的文件！"/>) 
                return;
            }
            if(upFilesName.indexOf('.rar')!==-1&&upFilesName.length!==1){
                popup(<Snackbar message="一次只允许上传一个.rar格式的文件！"/>)
                return;
            }
            if(upFilesName.indexOf('.txt')!==-1&&data.objectId.length==0){
                popup(<Snackbar message="请选择对象名称！"/>) 
                return;
            }
            this.setState({progressBool:true});
            return data;
            }
    }
    afterUploadSubmit(data){
        data.status == '200' ? popup(<Snackbar message={data.msg}/>) +this.uploadSuccess(): popup(<Snackbar
        message={data.msg}/>)+this.uploadFail()
    }
    uploadSuccess(){
        let indexCorpus=1;
        let pageStart=0;
        let sizeCorpus=10;
        let searchTableBool=false;
        this.resetUp();
        this.resetSearchCorpus();
        this.setState({uploadCorpusVisible: false,indexCorpus,pageStart,sizeCorpus,searchTableBool},()=>{this.fetchObject();this.fetchCorpusSet();});
    }
    uploadFail(){
        this.setState({progressBool: false});
    }
    //添加分类体系语料集
    showAddCorpusSetModal(){
        this.fetchClassify();
        this.setState({ addCorpusSetVisible : true });
    }
    closeAddCorpusSet(){
        this.resetAdd();
        this.setState({ addCorpusSetVisible : false });
    }
    beforeAddCorpusSetSubmit(data){
        data.functionId=this.state.functionsTypesId;
        data.name=this.trim(data.name);
         if(data.name==""||data.classifyId==""){
            popup(<Snackbar message="请完善输入信息！"/>) 
            return;
        }
        return data;
    }
    afterAddCorpusSetSubmit(data){
        this.resetAdd();
        this.setState({ addCorpusSetVisible : false,indexCorpusSet:1 });
        data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchCorpusSet(): popup(<Snackbar
        message={data.msg}/>)
    }
     trim(s){
        return s.replace(/(^\s*)|(\s*$)/g, "");
    }
    //编辑语料集
    editCorpusSet(value){
        let editShowCorpusSet=value;
        this.fetchClassify();
        this.setState({ editCorpusSetVisible : true,editShowCorpusSet });
    }
    closeEditCorpusSet(){
        this.resetEdit();
        this.setState({ editCorpusSetVisible : false });
    }
    beforeEditCorpusSetSubmit(data){
        data.corpusSetId=this.state.editShowCorpusSet.id;
        data.corpusSetName=this.trim(data.corpusSetName);
        if(data.corpusSetName==""){
            popup(<Snackbar message="请输入新的语料集名称！"/>) 
            return;
        }
        data.functionId=this.state.functionsTypesId;
        return data;
    }
    afterEditCorpusSetSubmit(data){
        this.resetEdit();
        this.setState({ editCorpusSetVisible : false });
        data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchCorpusSetPage(): popup(<Snackbar
        message={data.msg}/>)
    }
    //下载语料集
    downCorpusSet(value){
        let {objectBool,objectId,objectsData,colorIndex}=this.state;
        let corpusSetId = value.id;
        if(objectBool){
            if(objectsData[colorIndex].courpusNum== 0){
                popup( <Snackbar message={`该对象不存在语料`} /> );
            }else{
                window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/corpusSet/downloadCorpusSet?corpusSetId=${corpusSetId}&objectId=${objectId}`,'_self');
            } 
        }else{
            if(value.corpusNum == 0){
                popup( <Snackbar message={`该语料集不存在语料`} /> );
            }else{
                window.open(`${Uniplatform.context.url}/nlap/admin/corpusMgmt/corpusSet/downloadCorpusSet?corpusSetId=${corpusSetId}`,'_self');
            } 
        }
    }
    //删除语料集
    delCorpusSet(value){
        this.setState({corpusSetId:value.id});
        const cancelBtn = { text: "取消" };
        const approveBtn = {
            text: "确定",
            type: "warning",
            onClick: this.handleCorpusSetApprove.bind(this)
        }
        popup(<Dialog
            message="确定删除此语料集？" 
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }
    //确认删除语料集
    handleCorpusSetApprove(after){
        let {corpusSetId,totalCorpusSet,sizeCorpusSet,indexCorpusSet}=this.state;
        let param = new FormData();
            param.append('corpusSetId',corpusSetId);
         fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/corpusSet/deleteCorpusSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
            .then((res) => res.json())
            .then((data) => {
                if(totalCorpusSet%sizeCorpusSet==1&&indexCorpusSet==Math.ceil(totalCorpusSet/sizeCorpusSet) ){
                    this.setState({indexCorpusSet:1});
                }
                data.status == '200' ? popup(<Snackbar message={data.msg}/>) + this.fetchCorpusSetPage() : popup(<Snackbar
                message={data.msg}/>)
            }).catch((err) => {
            console.log(err.toString());
        });
         after( true );
    }
    //清空回显
    formTirggerUp( trigger ) {
        this.resetUp = trigger.reset;
    }
    formTirggerAdd(trigger){
         this.resetAdd = trigger.reset;
    }
    formTirggerEdit(trigger){
        this.resetEdit = trigger.reset;
    }
    searchTirggerCorpus(trigger){
        this.resetSearchCorpus = trigger.reset;
    }
    //分页
    exchangePageClassify(indexCorpusSet){
         this.setState({indexCorpusSet},()=>this.fetchCorpusSetPage());  
    }
    fetchCorpusSetPage(){
        if(this.state.resultSetBool){
            this.fetchCorpusSet();
        }else{
            this.searchCorpusSet();
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
        this.state.objectBool?this.fetchCorpusObject():this.fetchCorpusClassify();
    }
    //获取分类体系下的全部语料
    fetchCorpusClassify(){
        let {corpusSetId,sizeCorpus,indexCorpus,totalCorpus,corpusData,resultBool}=this.state;
        let param = new FormData();
        param.append('corpusSetId',corpusSetId);
        param.append('pageIndex',indexCorpus);
        param.append('pageSize',sizeCorpus);
        param.append('needText',true);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/listCorpusByCorpusSet', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            corpusData=data.corpus;
            this.handleSelectFetch(corpusData);
            totalCorpus=data.totalNumber;
            resultBool=true;
            this.setState({corpusData,totalCorpus,resultBool},()=>this.showObjects());
        }).catch((err) => console.log(err.toString()));
    }
    //获取对象下的语料
     fetchCorpusObject(){
        let {corpusData,totalCorpus,objectId,sizeCorpus,indexCorpus,corpusSetId,resultBool}=this.state;
        let param = new FormData();
        param.append('objectId',objectId);
        param.append('pageIndex',indexCorpus);
        param.append('pageSize',sizeCorpus);
        param.append('corpusSetId',corpusSetId);
        param.append('needText',true);
        fetch(Uniplatform.context.url + '/nlap/admin/corpusMgmt/classify/listCorpusBySetAndObject', {
            credentials: 'same-origin',
            method: 'POST',
            body:param
        })
        .then((res) => res.json())
        .then((data) => {
            corpusData=data.corpus;
            this.handleSelectFetch(corpusData);
            totalCorpus=data.totalNumber;
            resultBool=true;
            this.setState({corpusData,totalCorpus,resultBool},()=>this.showObjects());
        }).catch((err) => console.log(err.toString())); 
    }
    //查看语料
    beforeViewCorpusSubmit(data){
        let {corpusContent,corpusSetId}=this.state;
        data.corpusId=corpusContent._id;
        data.corpusSetId=corpusSetId;
        data.oldObjectId=corpusContent.higherLevelId;
        return data;
    }
    afterViewCorpusSubmit(data){
        data.status == '200' ? popup(<Snackbar message={data.msg}/>)+this.fetchCorpus()+this.fetchObject()+ this.setState({viewCorpusVisible:false}): popup(<Snackbar
        message={data.msg}/>)
    }
    closeviewCorpusSet(){
        this.setState({viewCorpusVisible:false});
    }
    render(){
        let {corpusTypes,corpusTypesIndex,functionsTypes,corpusSetData,functionsTypeIndex,bodyHtml,uploadCorpusVisible,objectsData,objectsDataAll,colorIndex,objectId,viewCorpusVisible,addCorpusSetVisible,editCorpusSetVisible,classifyData,editShowCorpusSet,progressBool,corpusContent} = this.state;
        return(
            <Page>
                {/*<COMM_HeadBanner prefix=" " />
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>*/}
                <Row style={{minHeight:'680px',margin:'0px'}}>
                    {/*<Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'910px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <NLPMenu url={'/nlap/platform/corpusManage'}/>
                    </Col>*/}
                    <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{padding:'0',marginTop:'20px'}}>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col">
                                <div className = "corpusManage_title" >
                                    <span className = "corpusManage_span">语料管理</span>
                                </div>
                            </Col>
                            <div className="corpusManage_content">
                                {
                                   corpusTypes.map((value,index)=>{     
                                        return(
                                            <div key={index} style={{backgroundColor:`${corpusTypes[index].status?'#ceeefc':'#fff'}`,border:`${corpusTypes[index].status?'solid 1px #6fccf5':'1px solid #e4e4e4'}`}} 
                                                className="cortype hang" onClick={this.clickCorpusTypes.bind(this,index)}>
                                                <img src={"../image/dic_icon"+`${Number(index)+1}`+".png"} style={{margin:'7% 5%',display:'inline-block'}}/>
                                                <span className="cortype_text" style={{color:'#da8b5c'}}>
                                                    {corpusTypes[index].name}<br/>({corpusTypes[index].corpusNum})
                                                </span>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                        </Row>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="corpusManage_col">
                                <div className = "corpusManage_title" >
                                    <span className = "corpusManage_span">语料列表</span>
                                </div>
                            </Col>
                            {bodyHtml}
                        </Row>
                    </Col>
                </Row>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/corpusMgmt/classify/uploadCorpus'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeUploadSubmit.bind(this)}
                          onAfterSubmit={this.afterUploadSubmit.bind(this)}
                    >
                    <Modal visible={ uploadCorpusVisible } size="middle" onClose={ this.closeUploadCorpus.bind( this ) }>
                        <ModalHeader>
                            上传语料
                        </ModalHeader>
                        <ModalBody>
                           <FormItem>
                               <Label>对象名称</Label>
                                {
                                    <Select placeholder="请选择对象名称" search={ true } name="objectId" value={`${objectId}`} showClear={ false }>
                                        {
                                            Object.keys(objectsDataAll).map((index)=>{ 
                                                return(
                                                    <Option value={objectsDataAll[index].id} key={index}>{objectsDataAll[index].name} </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                }
                            </FormItem>
                            <FormItem>
                                <Label>上传文件</Label>
                                <Upload placeholder="选择文件" name="files" multiple trigger={ this.formTirggerUp }/>
                            </FormItem>
                            {
                                progressBool?
                                <Progress type="loading" />
                                :
                                null
                            }
                            <span className="upExamples"> 1、 系统支持批量.txt 格式的文本文件进行上传。<br/>
                            2、 系统支持单个.zip,.rar 格式的文本文件进行上传。<br/>
                            3、 系统不支持不同格式的文本文件同时上传。<br/>
                            </span>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeUploadCorpus.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">上传</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                        action={contextUrl + '/nlap/admin/corpusMgmt/corpusSet/addCorpusSet'}
                        async={true}
                        type="horizontal"
                        enctype="multipart/form-data"
                        onSubmit={this.beforeAddCorpusSetSubmit.bind(this)}
                        onAfterSubmit={this.afterAddCorpusSetSubmit.bind(this)}
                        trigger={ this.formTirggerAdd }
                >
                    <Modal visible={ addCorpusSetVisible } size="middle" onClose={ this.closeAddCorpusSet.bind( this ) }>
                        <ModalHeader>
                            新建语料集
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Col size={{ normal: 8, small: 24, medium: 8, large: 8 }}><Label>语料集名称</Label></Col>
                                <Col size={{ normal: 16, small: 24, medium: 16, large: 16 }}>
                                    <Input name="name" placeholder="请输入语料集名称" />
                                </Col>
                            </FormItem>
                           <FormItem>
                                <Col size={{ normal: 8, small: 24, medium: 8, large: 8 }}><Label>分类体系</Label></Col>
                                <Col size={{ normal: 16, small: 24, medium: 16, large: 16 }}>
                                {
                                    <Select placeholder="请选择分类体系" search={ true } name="classifyId" showClear={ false }>
                                        {
                                            Object.keys(classifyData).map((index)=>{ 
                                                return(
                                                    <Option value={classifyData[index].id} key={index}>{classifyData[index].name} </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                }
                                </Col>
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeAddCorpusSet.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                <Form method="post"
                          action={contextUrl + '/nlap/admin/corpusMgmt/corpusSet/updateCorpusSet'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeEditCorpusSetSubmit.bind(this)}
                          onAfterSubmit={this.afterEditCorpusSetSubmit.bind(this)}
                          trigger={ this.formTirggerEdit }
                    >
                    <Modal visible={ editCorpusSetVisible } size="middle" onClose={ this.closeEditCorpusSet.bind(this) }>
                        <ModalHeader>
                            编辑语料集
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <Label>语料集名称</Label>
                                <Input name="corpusSetName" placeholder="请输入语料集名称" value={editShowCorpusSet.name}/>
                            </FormItem>
                            <FormItem>
                                <Label>体系名称</Label>
                                {
                                    Number(editShowCorpusSet.corpusNum)>0? 
                                    <Select placeholder="请选择分类体系" value={[`${editShowCorpusSet.classifyId}`]} search={ true } name="classifyId" disabled showClear={ false }>
                                        {
                                            Object.keys(classifyData).map((index)=>{ 
                                                return(
                                                    <Option value={classifyData[index].id} key={index}>{classifyData[index].name} </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                    :
                                    <Select placeholder="请选择分类体系" value={[`${editShowCorpusSet.classifyId}`]} search={ true } name="classifyId" showClear={ false }>
                                        {
                                            Object.keys(classifyData).map((index)=>{ 
                                                return(
                                                    <Option value={classifyData[index].id} key={index}>{classifyData[index].name} </Option>
                                                )
                                            })
                                        }
                                    </Select>
                                }
                            </FormItem>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeEditCorpusSet.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                {
                    viewCorpusVisible?
                    <Form method="post"
                          action={contextUrl + '/nlap/admin/corpusMgmt/classify/editCorpusObject'}
                          async={true}
                          type="horizontal"
                          enctype="multipart/form-data"
                          onSubmit={this.beforeViewCorpusSubmit.bind(this)}
                          onAfterSubmit={this.afterViewCorpusSubmit.bind(this)}
                          trigger={ this.formTirggerEdit }
                    >
                    <Modal visible={ viewCorpusVisible } size="large" onClose={ this.closeviewCorpusSet.bind(this) }>
                        <ModalHeader>
                            查看语料
                        </ModalHeader>
                        <ModalBody>
                            <FormItem>
                                <span>分类标签:</span>
                                <Select search={ true } name="objectId" value={[`${corpusContent.higherLevelId}`]} showClear={ false }>
                                    {
                                        Object.keys(objectsDataAll).map((index)=>{ 
                                            return(
                                                <Option value={objectsDataAll[index].id} key={index}>{objectsDataAll[index].name} </Option>
                                            )
                                        })
                                    }
                                </Select>
                            </FormItem>
                            <p style={{textAlign:'left'}}>{corpusContent.text}</p>
                            <Divider/>
                        </ModalBody>
                        <ModalFooter>
                            <Button onClick={this.closeviewCorpusSet.bind(this)}>关闭</Button>
                            <Button type="primary" htmlType="submit">确定</Button>
                        </ModalFooter>
                    </Modal>
                </Form>
                :
                null
                }
                {/*<Footer />*/}
            </Page>
        );
    };
}

CorpusManage.UIPage = page;
export default CorpusManage;