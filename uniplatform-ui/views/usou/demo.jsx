import React, {Component} from "react";
import {COMM_HeadBanner, Footer,InputAuto} from "../../components/uniplatform-ui";
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
    Prediction
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

class DemoApp extends Component {
    constructor(props){
        super(props);
        this.state = {
            data:[],
            total:0,
            index:1,
            visible:false,
            beginDate:null,
            endDate:null
        };
        this.beginSearch=this.beginSearch.bind(this);
        this.formGetter=this.formGetter.bind(this);
        this.handleChange=this.handleChange.bind(this);
        this.advanceSearch=this.advanceSearch.bind(this);
        this.beginDateChange=this.beginDateChange.bind(this);
        this.endDateChange=this.endDateChange.bind(this);
        this.selectAdvanceSearch=this.selectAdvanceSearch.bind(this);
        this.redirectDoc=this.redirectDoc.bind(this);
        this.getData=this.getData.bind( this );
        this.handleKeyPress=this.handleKeyPress.bind(this);
    }
    handleKeyPress( event ) {
        if (event.which==13){
            this.beginSearch();
        }
    }
    beginSearch(){
        this.setState({index:1});
        let pageNo=0;
        let body = this.createBody(pageNo);
        this.search(body);
        fetch( Uniplatform.context.usou_server + "/searchList/count",{method:'post',body:body})
            .then((response)=>response.json())
            .then((responseData)=>{this.setState({total:responseData.data})})
            .catch((err)=>console.log(err));
    }

    selectAdvanceSearch(){
        this.setState({visible:true});
    }
    advanceSearch(){
        this.setState({index:1});
        let pageNo=0;
        let all = this.getAll();
        let any = this.getAny();
        let not = this.getNot();
        let dsl = {};
        if(this.nullEmpty(all)){
            dsl.all=all;
        }else {
            dsl.all="";
        }
        if(this.nullEmpty(any)){
            dsl.any=any;
        }else {
            dsl.any="";
        }
        if(this.nullEmpty(not)){
            dsl.not=not;
        }else {
            dsl.not="";
        }
        let module=0;
        let formData = new FormData();

        if (this.nullEmpty(this.state.beginDate)){
            formData.append("beginTime",this.state.beginDate);

        }

        if (this.nullEmpty(this.state.endDate)){
            formData.append("endTime",this.state.endDate);
        }
        formData.append("dsl",JSON.stringify(dsl));
        formData.append("module",module);
        formData.append("pageNo",pageNo);

        this.search(formData);

        fetch( Uniplatform.context.usou_server + "/searchList/count",{method:'post',body:formData})
            .then((response)=>response.json())
            .then((responseData)=>{this.setState({total:responseData.data})})
            .catch((error)=>console.log(error));

        this.setState({visible:false})

    }

    nullEmpty(value){
        if (value==""||value==null||value==undefined){
            return false;
        }
        return true;
    }

    search(formData){

        fetch( Uniplatform.context.usou_server + "/searchList",{method:'post',body:formData})
            .then((response)=>response.json())
            .then((responseData)=>{
                this.setState({data:responseData.data});
            })
            .catch((error)=>console.log(error));
    }

    createBody(pageNo){
        let value = this.getValue();
        let module=0;
        let dsl = {};
        dsl.all=value;
        dsl.any="";
        dsl.not="";
        let formData = new FormData();
        formData.append("dsl",JSON.stringify(dsl));
        formData.append("module",module);
        formData.append("pageNo",pageNo);
        formData.append("query",value);

        return formData;
    }

    formGetter(getter){
        this.getValue=getter.value;
    }

    handleChange(index,size){
        let body = this.createBody(index-1);
        this.setState({index:index});
        this.search(body);
    }

    handleClose(){
        this.setState({visible:false})
    }

    beginDateChange(dateString, isValid, date){
        this.setState({beginDate:dateString});
    }

    endDateChange(dateString, isValid, date){
        this.setState({endDate:dateString})
    }

    formGetterAll(getter){
        this.getAll=getter.value;
    }
    formGetterAny(getter){
        this.getAny=getter.value;
    }
    formGetterNot(getter){
        this.getNot=getter.value;
    }

    parsesource(source,publish_time){
        return source==""?publish_time:source+"  "+publish_time
    }

    parseSummary(summary){
        return summary.length>150?summary.substring(0,150)+"...":summary+"..."
    }

    redirectDoc(event){
        const newsId = event.target.getAttribute('value');
        let query = this.getValue();
        let formData = new FormData();
        formData.append("query",query);
        formData.append("docId",newsId);
        fetch( Uniplatform.context.usou_server + "/searchList/click",{method:'post', body: formData} )
            .catch((err)=>console.log(err));

    }

   
    getData( value ) {
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

    render() {
        let {data,total,index,visible} = this.state;

        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                <Container type="fixed" style={{minHeight: '680px'}}>
                    <Row style={{marginTop:'20px'}}>
                        <Col size={10}>
                            <Prediction placeholder="请输入" dataSource={ this.getData } delay={ 100 } clear 
                                onKeyPress={ this.handleKeyPress }
                             style={{minHeight: '2.5rem'}}  name="prediction" getter={ this.formGetter } />
                        </Col>
                        <Col size={8}>
                            <Button type="primary" size="large" onClick={this.beginSearch}>搜索</Button>
                            <Button type="primary" size="large" onClick={this.selectAdvanceSearch} >高级搜索</Button>
                        </Col>
                        <Col size={11}/>
                    </Row>
                   
                    <Row>
                        {
                            data.map((item,index)=>{
                                let summary = this.parseSummary(item.fl_content);
                                let source = this.parsesource(item.fl_site_name,item.fl_publish_time);
                                let title = item.fl_title;
                                return (<Col style={{marginTop: '20px'}} key={index}>
                                    <Row><a target="_blank"
                                            href={item.fl_url}
                                            value={item._id}
                                            style={{fontSize: 'initial',paddingLeft: '15px'}}
                                            dangerouslySetInnerHTML={{ __html: title}}
                                            onClick={this.redirectDoc}
                                    >
                                    </a>
                                    </Row>
                                    <Row><Col>{source}</Col>
                                    </Row>
                                    <Row><Col size={20}><q dangerouslySetInnerHTML={{__html:summary}}/></Col>
                                    </Row></Col>)
                            })
                        }
                    </Row>
                    <Pagination style={{marginTop:'40px',marginBottom: '50px'}} index={ index }  total={ total>1000?1000:total } pages={ 10 } onChange={this.handleChange} />
                </Container>

                <Modal visible={ this.state.visible } onClose={ this.handleClose.bind( this ) } >
                    <ModalHeader>
                        高级搜索
                    </ModalHeader>
                    <ModalBody height={ 500 }>
                        <p style={{fontSize: 'large', color: 'blue'}}>搜索设置</p>
                        <Row >
                            <Col size={7}>
                                <p>包含以下所有关键词</p></Col>
                            <Col style={{paddingLeft: '0px', paddingRight:'0px'}}  size={11}>
                                <Input style={{marginTop: '8px'}} placeholder=""
                                       getter={ this.formGetterAll.bind(this) }/>
                            </Col>
                            <Col size={6}>
                                <p style={{color: 'brown'}}>多个词以空格分隔</p>
                            </Col>
                        </Row>

                        <Row >
                            <Col size={7}>
                                <p>包含以下任意关键词</p></Col>
                            <Col style={{paddingLeft: '0px', paddingRight:'0px'}}  size={11}>
                                <Input style={{marginTop: '8px'}} placeholder=""
                                       getter={ this.formGetterAny.bind(this) }/>
                            </Col>
                            <Col size={6}>
                                <p style={{color: 'brown'}}>多个词以空格分隔</p>
                            </Col>
                        </Row>

                        <Row >
                            <Col size={7}>
                                <p>不包含以下所有关键词</p></Col>
                            <Col style={{paddingLeft: '0px', paddingRight:'0px'}}  size={11}>
                                <Input style={{marginTop: '8px'}} placeholder=""
                                       getter={ this.formGetterNot.bind(this) }/>
                            </Col>
                            <Col size={6}>
                                <p style={{color: 'brown'}}>多个词以空格分隔</p>
                            </Col>
                        </Row>
                        <p style={{fontSize: 'large', color: 'blue'}}>时间设置</p>
                        <Row >
                            <Col size={7}>
                                <p>开始日期</p></Col>
                            <Col style={{paddingLeft: '0px', paddingRight:'0px'}}  size={11}>
                                <DateTimePicker onChange={this.beginDateChange}/>
                            </Col>
                        </Row>
                        <Row >
                            <Col size={7}>
                                <p>结束日期</p></Col>
                            <Col style={{paddingLeft: '0px', paddingRight:'0px'}}  size={11}>
                                <DateTimePicker onChange={this.endDateChange}/>
                            </Col>
                        </Row>

                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                        <Button type="primary" htmlType="submit" onClick={this.advanceSearch} >确定</Button>
                    </ModalFooter>
                </Modal>
                <Footer/>
            </Page>
        );

    }
}

DemoApp.UIPage = page;
export default DemoApp;
