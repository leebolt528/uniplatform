import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '主题相关',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ],
    js: [
        '../js/gauge.min.js',
        '../js/themeRelated.min.js'
    ]
};

class ThemeRelated extends Component {
    constructor(props){
        super(props);

        this.state = {
            isShow : 'none',
            similarity : 3
        }
        this.formGetter_one = this.formGetter_one.bind(this);
        this.formGetter_two = this.formGetter_two.bind(this);
    }

    componentDidMount() {
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    formGetter_one( getter ) {
        this.getValueOne = getter.value;
    }

    formGetter_two( getter ) {
        this.getValueTwo = getter.value;
    }

    handleValue() {
        this.setState({correlationSum:undefined});
        const criterion = this.getValueOne();
        const text = this.getValueTwo();
        if(!criterion.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要对比的文本"/>);
            return;
        }else if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要对比的文本"/>);
            return;
        }
        const flay = this.getValue();
        let param = new FormData();
        param.append('criterion', criterion);
        param.append('text', text);
        param.append('flay', flay);
        fetch(Uniplatform.context.url + '/nlap/admin/theme/relation', {
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
                let correlationSum = (data.correlationSum*100).toFixed(2);
                gauge(correlationSum);
                let isShow = 'inline';
                this.setState({isShow,correlationSum});
            }).catch((err) => console.log(err.toString()));
    }




    render() {
        let {isShow,correlationSum} = this.state;
        const content1 = '一带一路旨在借用古代丝绸之路的历史符号，高举和平发展的旗帜，积极发展与沿线国家的经济合作伙伴关系，共同打造政治互信、经济融合、文化包容的利益共同体、命运共同体和责任共同体。';
        const content2 = '一带一路是促进共同发展、实现共同繁荣的合作共赢之路，旨在借用古代丝绸之路的历史符号，高举和平发展的旗帜，积极发展与沿线国家的经济合作伙伴关系。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/themeRelated'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in16.png"/><span className="classTitle">主题相关</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                <p style={{margin:'30px 0px 0px 30px',fontWeight:'600',color:'#70b4e4'}}>标准</p>
                                <Textarea style={{margin:'0px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                          getter={ this.formGetter_one } value={content1}/>
                            </Col>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                <p style={{margin:'30px 0px 0px 30px',fontWeight:'600',color:'#70b4e4'}}>判断文本</p>
                                <Textarea style={{margin:'0px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                          getter={ this.formGetter_two } value={content2}/>
                            </Col>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>检测</Button>
                                <Select value="text" showClear={false} getter={ this.formGetter.bind(this) }
                                        style={{width:'20%',display:'inline-block',float:'right',marginRight:'10px'}}>
                                    <Option value="text">句子</Option>
                                    <Option value="word">词</Option>
                                </Select>
                            </Col>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">检测结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{textAlign:'center',margin:'20px 23px',width:'95%'}}>
                                    <div className="container" style={{display:isShow}}>
                                        {correlationSum ? null : <Loading />}
                                        <canvas id="gauge-ps"></canvas>
                                    </div>
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
ThemeRelated.UIPage = page;
export default ThemeRelated;