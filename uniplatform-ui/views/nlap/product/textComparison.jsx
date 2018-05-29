import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '文本对比',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class TextComparison extends Component {
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
        this.formGetter_one = this.formGetter_one.bind(this);
        this.formGetter_two = this.formGetter_two.bind(this);
    }

    componentDidMount() {
    }

    formGetter_one( getter ) {
        this.getValueOne = getter.value;
    }

    formGetter_two( getter ) {
        this.getValueTwo = getter.value;
    }

    handleValue() {
        this.setState({leftHtml:undefined, rightHtml:undefined});
        const text1 = this.getValueOne();
        const text2 = this.getValueTwo();
        if(!text1.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要对比的文本"/>);
            return;
        }else if(!text2.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要对比的文本"/>);
            return;
        }
        let show = true;
        this.setState({show});
        let param = new FormData();
        param.append('text1', text1);
        param.append('text2', text2);
        fetch(Uniplatform.context.url + '/nlap/admin/text/comparison/compare', {
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
                let diffs = data.diffs;
                this.setState({diffs});
                this.showDiffs();
            }).catch((err) => console.log(err.toString()));
    }

    showDiffs(){
        let {diffs} = this.state;
        let leftHtml = [], rightHtml = [];
        for(let i=0; i<diffs.length; i++){
            if(diffs[i].operation != 'INSERT'){
                if(diffs[i].operation == 'DELETE'){
                    leftHtml.push(
                        <span key={i} style={{backgroundColor:'#ffa34a'}}>{diffs[i].text}</span>
                    );
                }else {
                    leftHtml.push(
                        <span key={i}>{diffs[i].text}</span>
                    );
                }
            }
            if(diffs[i].operation != 'DELETE'){
                if(diffs[i].operation == 'INSERT'){
                    rightHtml.push(
                        <span key={i} style={{backgroundColor:'#9fe26e'}}>{diffs[i].text}</span>
                    );
                }else {
                    rightHtml.push(
                        <span key={i}>{diffs[i].text}</span>
                    );
                }
            }
        }
        this.setState({leftHtml,rightHtml});
    }




    render() {
        let {leftHtml,rightHtml,show} = this.state;
        const content1 = '　　中国是以华夏文明为源泉、中华文化为基础并以汉族为主体民族的多民族国家，通用汉语。中国人常以龙的传人、炎黄子孙自居。'+
            '中国是世界四大文明古国之一，有着悠久的历史，距今约5000年前，以中原地区为中心开始出现聚落组织进而形成国家，后历经多次民族交融和朝代更迭，' +
            '直至形成多民族国家的大一统局面。20世纪初辛亥革命后，君主政体退出历史舞台，共和政体建立。1949年中华人民共和国成立后，在中国大陆建立了人民代表大会制度的政体。'+
            '中国疆域辽阔、民族众多，先秦时期的华夏族在中原地区繁衍生息，到了汉代通过文化交融使汉族正式成型，奠定了中国主体民族的基础。 ' +
            '后又通过与周边民族的交融，逐步形成统一多民族国家的局面，而人口也不断攀升，宋代中国人口突破一亿，清朝时期人口突破四亿，到目前中国人口已突破十三亿 。'+
            '中国文化渊远流长、博大精深、绚烂多彩，是东亚文化圈的文化宗主国，在世界文化体系内占有重要地位，由于各地的地理位置、自然条件的差异，' +
            '人文、经济方面也各有特点。而且还有有多彩的民俗文化，传统艺术形式有诗词、戏曲、书法、国画等，而春节、元宵、清明、端午、中秋、重阳等是中国重要的传统节日。';
        const content2 = '　　中国是以华夏文明为源泉、中华文化为基础并以汉族为主体民族的多民族国家，通用汉语。中国人常以龙的传人、炎黄子孙自居。'+
            '中国是四大文明古国之一，有着悠久的历史，是世界国土面积第三大的国家，世界第一大人口国，与英、法、美、俄并为联合国安理会五大常任理事国。' +
            '中国是世界第二大经济体，世界第一贸易大国， 世界第一大外汇储备国， 世界第一大钢铁生产国和世界第一大农业国， 世界第一大粮食' +
            '总产量国以及世界上经济成长最快的国家之一。  第二大吸引外资国，还是世界许多国际组织的重要成员，被认为是潜在超级大国之一。'+
            '中国疆域辽阔、民族众多，先秦时期的华夏族在中原地区繁衍生息，到了汉代通过文化交融使汉族正式成型，奠定了中国主体民族的基础。 ' +
            '后又通过与周边民族的交融，逐步形成统一多民族国家的局面，而人口也不断攀升，宋代中国人口突破一亿，清朝时期人口突破四亿，到目前中国人口已突破十三亿 。'+
            '中国文化渊远流长、博大精深、绚烂多彩，中国拥有最丰富的世界文化遗产和自然人文景点，是世界旅游大国之一，也是东亚文化圈的文化宗主国，' +
            '在世界文化体系内占有重要地位，由于各地的地理位置、自然条件的差异，人文、经济方面也各有特点。而且还有有多彩的民俗文化，' +
            '传统艺术形式有诗词、戏曲、书法、国画等，而春节、元宵、清明、端午、中秋、重阳等是中国重要的传统节日。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/textComparison'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in4.png"/><span className="classTitle">文本对比</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                <p style={{margin:'30px 0px 0px 30px',fontWeight:'600',color:'#70b4e4'}}>TEXT 1</p>
                                <Textarea style={{margin:'0px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                          getter={ this.formGetter_one } value={content1}/>
                            </Col>
                            <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }}>
                                <p style={{margin:'30px 0px 0px 30px',fontWeight:'600',color:'#70b4e4'}}>TEXT 2</p>
                                <Textarea style={{margin:'0px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                          getter={ this.formGetter_two } value={content2}/>
                            </Col>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>对比</Button>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">对比结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content" style={{maxHeight:'500px',overflow:'auto'}}>
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    <Col size={11}>
                                        <p style={{fontWeight:'600',color:'#70b4e4'}}>TEXT 1</p>
                                        <div>{show ? (leftHtml ? leftHtml : <Loading />) : null}</div>
                                    </Col>
                                    <Col size={11} offset={2}>
                                        <p style={{fontWeight:'600',color:'#70b4e4'}}>TEXT 2</p>
                                        <div>{show ? (rightHtml ? rightHtml : <Loading />) : null}</div>
                                    </Col>
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
TextComparison.UIPage = page;
export default TextComparison;