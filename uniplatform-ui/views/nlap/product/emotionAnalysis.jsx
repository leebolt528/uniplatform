import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '情感分析',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ],
    js: [
        '../js/gauge.min.js',
        '../js/emotion_dashboard.min.js'
    ]
};

class EmotionAnalysis extends Component {
    constructor(props){
        super(props);

        this.state = {
            isShow : 'none',
            similarity : 3
        }
        this.formGetter = this.formGetter.bind( this );
    }

    componentDidMount() {
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    handleValue() {
        this.getSentimentScore();
        this.getSentimentAnalysis();
    }

    //获取情感倾向
    getSentimentScore(){
        this.setState({score:undefined});
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要分析的文本"/>);
            return;
        }
        let param = new FormData();
        param.append('text', text);
        fetch(Uniplatform.context.url + '/nlap/admin/sentimentAnalysis/analyse', {
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
                let score;
                if(data.score == '-1'){
                    score = 5;
                }else if(data.score == '0'){
                    score = 15;
                }else {
                    score = 25;
                }
                gauge(score);
                let isShow = 'inline';
                this.setState({isShow,score});
            }).catch((err) => console.log(err.toString()));
    }

    //获取情感元素
    getSentimentAnalysis(){
        this.setState({elementHtml:undefined});
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要分析的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text', text);
        fetch(Uniplatform.context.url + '/nlap/admin/sentimentAnalysis/emotion/ceils', {
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
                let element = data.data.map(function (item) {
                    let ele = {
                        sentence : item.sentence,
                        evals : item.evals
                    };
                    return ele;
                });
                this.setState({element});
                this.showElement();
            }).catch((err) => console.log(err.toString()));
    }

    //展示情感元素
    showElement(){
        let {element} = this.state;
        let elementHtml = [];
        let color = ['#74E7E7','#FF9999','#FF99CC','#CCFF66','#FFCC99','#336699','#FFFF66','#99CC66',
            '#666699','#ABCDEF','#99CC33','#FF9900','#99CC99','#FF0033','#FF9966','#CCFF00','#CC3399',
            '#FF6600','#FFFF99','#CCCCCC','#CC9999','#339999','#999966','#3399CC','#669999','#0099CC',
            '#CCCC99','#003366','#99CC00','#FFCCCC','#FFCC00','#CCFF99','#CC6600','#999999','#CCCC33',
            '#FF9933','#FFFFCC','#009933','#FFFF00','#CCFFCC','#CC6633','#99CCCC','#6699CC','#CC0066',
            '#009999','#FFCC33','#CCCCFF','#993333','#006699','#99CCFF','#0066CC','#339933','#66CC00'];
        let colorIndex = 0;
        for(let i=0; i<element.length; i++){
            elementHtml.push(
                <div key={i} style={{lineHeight:'30px'}}>
                    {element[i].sentence}
                    {element[i].evals.map(function (item,index) {
                        if(index>color.length-1){
                            colorIndex = index-color.length;
                        }else {
                            colorIndex = index;
                        }
                        return <span key={index} style={{margin:'6px',padding:'3px',backgroundColor:color[colorIndex]}}>{item}</span>
                    })}
                </div>
            );
        }
        this.setState({elementHtml});
    }



    render() {
        let {elementHtml,isShow,show,score} = this.state;
        const contents = '听别人介绍来的，最近太忙了，确认晚了。质量好，正品，款式好看，鞋子是很好的，跟图片上一样一样的，码子也很标准，包装也非常好， 跟专柜的一样的。鞋子很好，感觉物超所值！服务态度也好，全五分！';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'1090px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/emotionAnalysis'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in12.png"/><span className="classTitle">情感分析</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents}/>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>分析</Button>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">情感倾向分析</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%',textAlign:'center'}}>
                                    <div className="container" style={{display:isShow}}>
                                        {score ? null : <Loading />}
                                        <canvas id="gauge-ps"></canvas>
                                    </div>
                                </Col>
                            </div>
                        </Row>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">情感元素展示</span>
                                </div>
                            </Col>
                            <div className="dicManage_content" style={{maxHeight:'315px',overflow:'auto'}}>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    {show ?
                                        (elementHtml ? (elementHtml.length==0? <span>无结果</span> : elementHtml) : <Loading />)
                                        : null
                                    }
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
EmotionAnalysis.UIPage = page;
export default EmotionAnalysis;