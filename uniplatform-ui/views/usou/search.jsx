import React, {Component} from "react";
import {COMM_HeadBanner, Footer,InputAutoExpre,InputAutoPhrase} from "../../components/uniplatform-ui";
import {
    Button,
    Col,
    Container,
    DateTimePicker,
    Icon,
    Input,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader,
    Page,
    Pagination,
    Row,
    Tab,
    Tabs,
    Textarea,
    Prediction,
    Dropdown,
    RadioGroup,
    Radio,
    Form,
    FormItem
} from "epm-ui";


const contextUrl = '/uniplatform';
const page = {
    title: '首页',
    css: [
        'css/index.min.css',
        'css/singleBanner.min.css',
        'css/autocomplete.min.css'
    ]
};

class Search extends Component {
    constructor(props){
        super(props);
        this.state = {
            data:{result:[],count:0,status:200},
            index:1,
            beginDate:null,
            endDate:null,
            expreData:'',
            pageBool:true,
            defaultValue:'',
            currentTab:0,
            searchSet:{fun:'phrase',order:'relate'},
            firstBool:true,
            queryType:'KEYWORD',
            rankType:'SIMILARITY',
            firstInSearch:true
        };
        this.commonSearch=this.commonSearch.bind(this);
        this.clickCommonSearch=this.clickCommonSearch.bind(this);
        this.formGetter=this.formGetter.bind(this);
        this.beginDateChange=this.beginDateChange.bind(this);
        this.endDateChange=this.endDateChange.bind(this);
        this.handleKeyPressExpre=this.handleKeyPressExpre.bind(this);
        this.handleKeyPressPhrase=this.handleKeyPressPhrase.bind(this);
    }
    componentWillMount(){
        this.getValue=function(){
            return '';
        };
        //this.fetchDataSource();
    }
    componentDidMount() {
         this.fetchDataSource();
        //document.getElementsByClassName("container")[0].style.paddingTop=document.body.clientHeight/3+"px";
        document.getElementsByTagName('html')[0].style.overflow='hidden';
    }
    //获取表达式搜索初始化数据
    fetchDataSource(){
        let {expreData}=this.state;
        fetch(Uniplatform.context.usou_server + '/autofill/map', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            expreData=data.data;
            this.setState({expreData});
            });
    }
    handleKeyPressExpre( event ) {
        if (event.which==13&&document.getElementById('auto').className=='auto_hidden'){
           this.setState({index:1},()=>this.commonSearch());
        }
    }
    handleKeyPressPhrase(event){
        if (event.which==13){
            this.setState({index:1},()=>this.commonSearch());
        }
    }
    //表达式和短语搜索
    clickCommonSearch(){
        this.setState({index:1},()=>this.commonSearch());
    }
    commonSearch(){
        let {queryType,rankType,index}=this.state;
        //if(document.getElementById('o').value.length>0){ 
        if(document.getElementsByClassName('myInput')[0].value.replace(/^\s+|\s+$/,'').length>0){
            document.getElementsByTagName('html')[0].style.overflow='auto';
            this.setState({pageBool:false});
            let formData = new FormData();
            formData.append("word",document.getElementsByClassName('myInput')[0].value); 
            formData.append("queryType",queryType);
            formData.append("rankType",rankType);
            formData.append("pageNo",index-1);
            formData.append("pageSize",10); 
            fetch( Uniplatform.context.usou_server + "/search/perfectMatch",{method:'post', body: formData} )
                .then((responseData)=>responseData.json())
                .then((responseData)=>{
                    let data=responseData.data;
                    data.status=responseData.status;
                    this.setState({data,firstInSearch:false});
                })
                .catch((err)=>console.log(err));
        }else{
            document.getElementsByTagName('html')[0].style.overflow='hidden';
            this.setState({pageBool:true});
        }
    }
    //高级搜索
    beforeAdvanceSearchSubmit(data1){
        let {searchSet,queryType,rankType}=this.state;
        let index=1;
        let all=data1.all.replace(/^\s+|\s+$/,'').replace(/\s+/g,' ');
        let any=data1.any.replace(/^\s+|\s+$/,'').replace(/\s+/g,'|');
        let not=data1.not.replace(/^\s+|\s+$/,'').replace(/\s+/g,'|');
        let defaultValue=all+`${any.length>0?' +('+any+')':''}`+`${not.length>0?' -('+not+')':''}`;
        if(defaultValue.length>0){
            searchSet.fun=="phrase";
            queryType='KEYWORD';
            this.setState({defaultValue,searchSet,queryType,index:1,firstInSearch:false});
            this.closeSlide();
            this.resetAdvancesearch();

            let param = new FormData();
            param.append('word',defaultValue);
            param.append('queryType','DSL');
            param.append('rankType',rankType);
            param.append('pageNo',index-1);
            param.append('pageSize',10);
            fetch(Uniplatform.context.usou_server + '/search/perfectMatch', {
                credentials: 'same-origin',
                method: 'POST',
                body:param
            })
            .then((res) => res.json())
            .then((data1) => {
                let data=data1.data;
                data.status=data1.status;
                let pageBool=false;
                this.setState({data,pageBool});
            }).catch((err) => console.log(err.toString()));
        }else{
            document.getElementsByTagName('html')[0].style.overflow='hidden';
            this.setState({pageBool:true});
        }
    }
    //获取Input的值
    formGetter(value){
        //let defaultValue=document.getElementById('o').value;
        let defaultValue=document.getElementsByClassName('myInput')[0].value;
        this.setState({defaultValue});
        this.getValue=function(){
            return value;
        };
    }
    handleChange(index,size){
        let body = this.createBody(index-1);
        this.setState({index:index});
        this.search(body);
    }
    //分页
    exchangePage(index,size){
        this.setState({index},()=>this.commonSearch());

    }
    //高级设置日期
    beginDateChange(dateString, isValid, date){
        this.setState({beginDate:dateString});
    }

    endDateChange(dateString, isValid, date){
        this.setState({endDate:dateString})
    }
    //搜索设置getter和trigger
    formGetterSearch(getter){
        this.getSearch=getter.value;
    }
    formGetterOrder(getter){
        this.getOrder=getter.value;
    }
    formTirggerSearch(trigger){
        this.resetSearch = trigger.reset;
    }
    formTirggerOrder(trigger){
        this.resetOrder = trigger.reset;
    }
    //保存搜索设置
    saveSet(){
        let {searchSet,queryType,rankType}=this.state;
        searchSet.fun=this.getSearch();
        searchSet.order = this.getOrder();
        if(searchSet.fun=="phrase"){
            queryType="KEYWORD";
        }else{
            queryType="EXPRESSION";
        };
        if(searchSet.order=='relate'){
            rankType='SIMILARITY';
        }else if(searchSet.order=='time'){
            rankType='TIME';
        }else{
            rankType='COMPREHENSIVE';
        }
        this.closeSlide();
        this.setState({searchSet,queryType,rankType});
    }
    //搜索列表展示
    parsesource(article_from,publish_time){
        return (article_from==""||article_from==null||article_from==undefined)?publish_time:publish_time+' '+"来源:"+article_from
    }

    parseSummary(summary){
        return summary.length>150?summary.substring(0,150)+"...":summary+"..."
    }
    redirectDoc(event){
        const newsId = event.target.getAttribute('value');
        let query =document.getElementsByClassName('myInput')[0].value;
        let formData = new FormData();
        formData.append("query",query);
        formData.append("docId",newsId);
        fetch( Uniplatform.context.usou_server + "/document/click",{method:'post', body: formData} )
            .catch((err)=>console.log(err));
    }
    //设置
    openSlide(){
        var header=document.getElementById('slide');
        header.style.height='300px';
        header.style.transition='height 500ms';
    }
    clickSet(item){
        let {currentTab}=this.state;
        if(item.name=="搜索设置"){
           currentTab=0;
        }else{
           currentTab=1;
        }
        this.setState({currentTab},()=>this.openSlide());
    }
    closeSlide(){
        this.resetSearch();
        this.resetOrder();
        var header=document.getElementById('slide');
        header.style.height='0px';
        header.style.transition='height 500ms';
        this.resetAdvancesearch();
    }
    //返回搜索首页
    clickFirstPage(){
        $(document).scrollTop(0);
        document.getElementsByTagName('html')[0].style.overflow='hidden';
        this.setState({pageBool:true});

    }
    //请求短语匹配字段
    getDataPhrase( ) {
            let word = this.getValue();
            let preData = [];
            if(word==undefined || word==null || word.trim() == ""){
                return preData;
            }else{
                let formData = new FormData();
                formData.append("word",word.trim());     
                return fetch( Uniplatform.context.usou_server + "/suggest/word",{method:'post', body: formData} )
                            .then(function(response) {
                                return response.json();
                            })
                            .then(function(responseData){
                                var suggests = responseData;
                                preData=suggests.data;
                                return preData;
                            });
            }
        }

        clickClear(){
            this.setState({defaultValue:''});
        }

    render() {
        let {data,index,expreData,pageBool,defaultValue,currentTab,searchSet,firstInSearch} = this.state;
        let dataSourceDrop = [              
            {
                name: '搜索设置',               
                onClick: ( item )=>{  
                    this.clickSet(item);
                }
            },
            {
                name: '高级搜索',
                onClick: ( item)=>{
                    this.clickSet(item);
                }
            }
        ];
        return (
            <Page>
                <div id="slide" style={{overflow:'hidden',background:'#fafafa',height:'0px',position:'fixed',width:'100%',zIndex:'220',borderBottom:'1px solid #ccc'}}>
                    <i className='fa fa-times fa-lg' style={{position:'absolute',right:'60px',top:'12px'}} onClick={this.closeSlide.bind(this)}></i>
                    <Tabs currentTab={currentTab}>
                        <Tab title="搜索设置">
                            <Row style={{marginBottom:'5px'}}>
                                <Col size={5} style={{textAlign:'right',lineHeight:'32px'}}>搜索方式：</Col>
                                <Col size={3} style={{padding:'0px',lineHeight:'32px'}}>设定搜索方式 </Col>
                                <Col size={10} style={{padding:'0px',lineHeight:'32px'}}>
                                    <RadioGroup value={this.state.searchSet.fun} name="rGroup" type="inline" trigger={ this.formTirggerSearch.bind(this) } getter={ this.formGetterSearch.bind(this) }>
                                        <Radio value="phrase">短语</Radio>
                                        <Radio value="expression">表达式</Radio>
                                    </RadioGroup>
                                </Col>
                            </Row>
                            <Row style={{marginBottom:'5px'}}>
                                <Col size={5} style={{textAlign:'right',lineHeight:'32px'}}>排序设置：</Col>
                                <Col size={3} style={{padding:'0px',lineHeight:'32px'}}>设定搜索结果的排序方式</Col>
                                <Col size={10} style={{padding:'0px',lineHeight:'32px'}}>
                                    <RadioGroup value={this.state.searchSet.order} name="rGroup" type="inline" trigger={ this.formTirggerOrder.bind(this) } getter={ this.formGetterOrder.bind(this) }>
                                        <Radio value="relate">相关性排序</Radio>
                                        <Radio value="time">时间排序</Radio>
                                        <Radio value="com">综合排序</Radio>
                                    </RadioGroup>
                                </Col>
                            </Row>
                            <Row style={{marginBottom:'5px'}}>
                                <Col size={5} offset={8} style={{paddingTop:'50px'}}><Button type="primary" onClick={this.saveSet.bind(this)}>保存设置</Button></Col>
                            </Row>
                        </Tab>
                        <Tab title="高级搜索">
                            <Form method="post"
                                    async={true}
                                    type="horizontal"
                                    enctype="multipart/form-data"
                                    onSubmit={this.beforeAdvanceSearchSubmit.bind(this)}
                                    trigger={ ( trigger ) => {
                                        this.resetAdvancesearch = trigger.reset;
                                    } }
                            >
                                <Row style={{marginBottom:'5px'}}>
                                    <Col size={5} style={{textAlign:'right',lineHeight:'32px'}}>搜索结果：</Col>
                                    <Col size={3} style={{padding:'0px',lineHeight:'32px'}}>包含以下所有关键词</Col>
                                    <Col size={6}>
                                        <FormItem>
                                            <Input name='all'></Input>                                    
                                        </FormItem>
                                    </Col>
                                    <Col size={10} style={{padding:'0px',lineHeight:'32px',color:'brown',textAlign:'left'}}>多个词以空格分隔</Col>
                                </Row>
                                <Row style={{marginBottom:'5px'}}>
                                    <Col size={3} offset={5} style={{padding:'0px',lineHeight:'32px'}}>包含以下任意关键词</Col>
                                    <Col size={6}>
                                        <FormItem>
                                            <Input name='any'></Input>                                    
                                        </FormItem>
                                    </Col>
                                    <Col size={10} style={{padding:'0px',lineHeight:'32px',color:'brown',textAlign:'left'}}>多个词以空格分隔</Col>
                                </Row>
                                <Row style={{marginBottom:'5px'}}>
                                    <Col size={3} offset={5} style={{padding:'0px',lineHeight:'32px'}}>不包含以下关键词</Col>
                                    <Col size={6}>
                                        <FormItem>
                                            <Input name='not'></Input>                                    
                                        </FormItem>
                                    </Col>
                                    <Col size={10} style={{padding:'0px',lineHeight:'32px',color:'brown',textAlign:'left'}}>多个词以空格分隔</Col>
                                </Row>
                                <Row style={{marginBottom:'5px'}}>
                                    <Col size={5} style={{textAlign:'right',lineHeight:'32px'}}>时间设置：</Col>
                                    <Col size={3} style={{padding:'0px',lineHeight:'32px'}}>开始时间</Col>
                                    <Col size={6}>
                                        <FormItem>
                                            <DateTimePicker name='start' onChange={this.beginDateChange} disabled/>                                    
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{marginBottom:'5px'}}>
                                    <Col size={3} offset={5} style={{padding:'0px',lineHeight:'32px'}}>结束时间</Col>
                                    <Col size={6}>
                                        <FormItem>
                                            <DateTimePicker name='end' onChange={this.endDateChange} disabled/>                                    
                                        </FormItem>
                                    </Col>
                                </Row>
                                <Row style={{marginBottom:'5px'}}>
                                    <Col size={6} offset={8}>
                                        <Button type="primary" htmlType="submit">高级搜索</Button>
                                    </Col>
                                </Row>
                            </Form>
                        </Tab>
                    </Tabs>
                </div>
                {
                    pageBool?
                    <div>
                        <Row>
                            <Col size={12} offset={12} style={{textAlign:'right',paddingRight:'100px',marginTop:'5px'}}>
                                <Dropdown dataSource={ dataSourceDrop } >
                                    <Dropdown.Trigger>
                                        <a style={{lineHeight:'34.5px',marginLeft:'10px'}}>设置<i className="fa fa-sort-desc" aria-hidden="true"></i></a>
                                    </Dropdown.Trigger>
                                </Dropdown>
                            </Col>
                        </Row>
                        <Container type="fixed" style={{minHeight: '680px',textAlign: 'center'}}>
                            <div className="header-search">
                                <span className="header-usou" > USOU </span>
                                <span className="header-color" > 搜索引擎</span>
                            </div>
                            {
                                searchSet.fun=="phrase"?
                                <div className='predParent' style={{width:'740px',marginLeft:'auto',marginRight:'auto'}}>
                                    <InputAutoPhrase dataSource={this.getDataPhrase()}  clickClear={this.clickClear.bind(this)} getter={ this.formGetter } onKeyPress={ this.handleKeyPressPhrase }/>
                                    <Button type="primary" size="large" onClick={this.clickCommonSearch} style={{textAlign:'left',borderRadius:'0'}}>搜索</Button>
                                </div>
                                :
                                <div style={{width:'740px',marginLeft:'auto',marginRight:'auto'}}>
                                    <InputAutoExpre dataSource={expreData} clickClear={this.clickClear.bind(this)} getter={ this.formGetter } onKeyPress={ this.handleKeyPressExpre }/>
                                    <Button type="primary" size="large" onClick={this.clickCommonSearch} style={{textAlign:'left',borderRadius:'0'}}>搜索</Button>
                                </div>
                            }
                        </Container>
                    </div>
                    :
                    <div>
                        <div className="searchTopHead" style={{padding:'10px',textAlign:'left',background: '#fff',position:'fixed',zIndex:'200',width:'100%',boxShadow:' 0 0 6px 0 rgba(151,151,151,.6)'}}>
                            <Row>
                                <Col size={14} style={{padding:'0px'}}>
                                    {
                                        searchSet.fun=="phrase"?
                                        <div>
                                            <span className="header-usou" > USOU </span>
                                            <InputAutoPhrase dataSource={this.getDataPhrase()} value={defaultValue} clickClear={this.clickClear.bind(this)} getter={ this.formGetter } onKeyPress={ this.handleKeyPressPhrase }/>
                                            <Button type="primary" size="large" onClick={this.clickCommonSearch} style={{textAlign:'left',borderRadius:'0'}}>搜索</Button>
                                        </div>
                                        :
                                        <div>
                                            <span className="header-usou" > USOU </span>
                                            <InputAutoExpre dataSource={expreData} value={defaultValue} clickClear={this.clickClear.bind(this)} getter={ this.formGetter } onKeyPress={ this.handleKeyPressExpre }/>
                                            <Button type="primary" size="large" onClick={this.clickCommonSearch} style={{textAlign:'left',borderRadius:'0'}}>搜索</Button>
                                        </div>
                                    }
                                </Col>
                                <Col size={5} offset={5} style={{textAlign:'right',paddingRight:'100px'}}>
                                        <a style={{lineHeight:'34.5px',marginLeft:'10px'}} onClick={this.clickFirstPage.bind(this)}>首页</a>
                                        <Dropdown dataSource={ dataSourceDrop } >
                                        <Dropdown.Trigger>
                                            <a style={{lineHeight:'34.5px',marginLeft:'10px'}}>设置<i className="fa fa-sort-desc" aria-hidden="true"></i></a>
                                        </Dropdown.Trigger>
                                    </Dropdown>
                                </Col>
                            </Row>
                        </div>
                        <div style={{minHeight: '680px',paddingTop:'54.53px',marginLeft:'70px'}}>
                            <Row>
                                {
                                    data.status=='200'?
                                        data.result.length>0?
                                        <Col>
                                            <Row>
                                                {
                                                    data.result.map((item,index)=>{
                                                        let summary = this.parseSummary(item.plain_text);
                                                        let source = this.parsesource(item.article_from,item.publish_time);
                                                        let title = item.title;
                                                        return (
                                                            <Col style={{marginTop: '20px'}} key={index}>
                                                                <Row><a target="_blank"
                                                                        href={item.url}
                                                                        value={item._id}
                                                                        style={{fontSize: 'initial',paddingLeft: '15px'}}
                                                                        dangerouslySetInnerHTML={{ __html: title}}
                                                                        onClick={this.redirectDoc.bind(this)}
                                                                ></a>
                                                                </Row>
                                                                <Row><Col>{source}</Col>
                                                                </Row>
                                                                <Row>
                                                                    <Col size={20}><span dangerouslySetInnerHTML={{__html:summary}}/></Col>
                                                                </Row>
                                                            </Col>
                                                        )
                                                    })
                                                }
                                            </Row>
                                            <Row>
                                                <Pagination style={{marginTop:'40px',marginBottom: '50px'}} index={ index }  total={data.count>1000?1000:data.count } size={ 10 } onChange={this.exchangePage.bind(this)} />
                                            </Row>
                                        </Col>
                                        :
                                            firstInSearch?
                                            ''
                                            :
                                            <p className="nullSearch">无对应的数据..</p>
                                    :
                                    <p className="nullSearch">输入的检索格式不正确..</p>
                                }
                            </Row>
                        </div>
                    </div>
                }
                <Footer/>
            </Page>
        );

    }
}

Search.UIPage = page;
export default Search;
