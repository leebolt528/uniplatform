import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '繁简转换',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class SimpleConversion extends Component {
    constructor(props){
        super(props);

        this.state = {
            index:1,
            size:10,
            total:0,
            userList : [],
            visible:false,
            updateVisible:false,
            crawlerData: [],
            siteData: [],
            dateData: [],
            taskData: [],
            unsucTaskData:[]
        }
        this.formGetter = this.formGetter.bind( this );
    }

    componentDidMount() {
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    //简体中文
    simplifiedChinese() {
        this.setState({changeText:undefined});
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要转换的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        fetch(Uniplatform.context.url + '/nlap/admin/text/simplified/simplifiedChinese', {
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
                let changeText = data.data;
                this.setState({changeText});
            }).catch((err) => console.log(err.toString()));
    }

    //港澳繁體
    HongKongMacao() {
        this.setState({changeText:undefined});
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要转换的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        fetch(Uniplatform.context.url + '/nlap/admin/text/simplified/HongKong', {
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
                let changeText = data.data;
                this.setState({changeText});
            }).catch((err) => console.log(err.toString()));
    }

    //台灣正體
    TaiwanBody() {
        this.setState({changeText:undefined});
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要转换的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        fetch(Uniplatform.context.url + '/nlap/admin/text/simplified/TaiWai', {
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
                let changeText = data.data;
                this.setState({changeText});
            }).catch((err) => console.log(err.toString()));
    }




    render() {
        let {changeText,show} = this.state;
        const contents = '	北京东方国信科技股份有限公司（以下简称"东方国信公司"）成立于1997年，是信息产业部认定的软件企业和北京市中关村高新技术企业，' +
            '并通过了ISO9001:2000质量管理体系认证。东方国信公司长期致力于中国国内电信领域的BI（商业智能）、CRM（客户关系管理）、CTI等软件开发、' +
            '服务与解决方案，拥有多项软件产品专利，是中国联通和中国电信的业务支撑系统的核心合作厂商之一，也是中国铁通业务支撑的三家核心厂商之一。' +
            '同时，公司在软件外包和业务流程外包方面取得飞速发展，分别在大连和重庆建立软件服务外包基地。'+
            '	 东方国信公司拥有完整的企业级BI（商业智能）、CRM（客户关系管理）、CTI的系统解决方案，具有较为丰富的产品线和较强的交付能力，能够为电信' +
            '运营商提供适用的信息化解决方案，满足电信运营商在不同发展阶段的管理需求，并可实现平滑升级。东方国信公司在中国国内电信运营商的几十个省' +
            '分公司提供并实施BI和CRM项目的咨询服务及解决方案。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/simpleConversion'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in6.png"/><span className="classTitle">繁简转换</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents} />
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.simplifiedChinese.bind(this)}>简体中文</Button>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.HongKongMacao.bind(this)}>港澳繁體</Button>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.TaiwanBody.bind(this)}>台灣正體</Button>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">转换结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content" style={{maxHeight:'500px',overflow:'auto'}}>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    <span style={{fontSize:'12px'}}>
                                        {show ? (changeText ? changeText : <Loading />) : null}
                                    </span>
                                </Col>
                            </div>
                        </Row>
                    </Col>
                </Row>

                <Footer />
            </Page>
        );
    }

}
SimpleConversion.UIPage = page;
export default SimpleConversion;