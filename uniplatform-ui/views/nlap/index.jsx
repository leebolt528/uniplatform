import React, { Component } from 'react';
import { Container,Icon,Carousel,Button } from 'epm-ui';
import { COMM_HeadBanner, Footer } from '../../components/uniplatform-ui';

const page = {
    title: '首页',
    css: [
        'css/first_index.min.css',
    ]
};

class IndexApp extends Component {

    getProdect(){
        window.open(`/nlap/product/autoWordSegment`,'_self');
    }

    getPlatform(){
        window.open(`/nlap/platform/dictionary`,'_self');
    }

    render() {
        return (
            <Container type="fluid">
                <COMM_HeadBanner prefix="非结构化统一平台"/>

                <div className="inner-content">
                    <div className="inner-contentUp">
                        <div className="inner-contentUp-top">
                            <div className="inner-contentUp-topL">
                                <p>NLAP是东方国信自主研发的一款自然语言处理和分析系统，能对词、句、篇进行智能分析，满足用户文本挖掘等需求，系统为用户提供实体抽取、文本分类、事件抽取、情感分析等丰富功能接口。</p>
                                <Button className="btn" style={{backgroundColor:'#08102a',color:'white'}} onClick={this.getProdect.bind(this)}>
                                    产品功能
                                </Button>
                                <Button className="btn" style={{backgroundColor:'#1dc1cf',color:'white'}} onClick={this.getPlatform.bind(this)}>
                                    管控平台
                                </Button>
                            </div>
                        </div>
                        <div className="inner-contentUp-bottom">
                            <h2>应用场景</h2>
                        </div>
                        <div className="inner-contentUp-banner-first">
                            <div className="inner-contentUp-banner-first-right">
                                <h2>舆情分析</h2>
                                <p>舆情分析应用是通过NLAP对互联网数据进行语义、篇章分析，达到舆情预警、舆情监控的目的</p>
                            </div>
                        </div>
                        <div className="inner-contentUp-banner-second">
                            <div className="inner-contentUp-banner-second-left">
                                <h2>电商运营</h2>
                                <p>NLAP对商家商品评论进行语义情感分析，对商家运营做决策支撑</p>
                            </div>
                        </div>
                        <div className="inner-contentUp-banner-third">
                            <div className="inner-contentUp-banner-third-right">
                                <h2>情报分析</h2>
                                <p>舆情分析应用是通过NLAP对互联网数据进行语义、篇章分析，达到舆情预警、舆情监控的目的</p>
                            </div>
                        </div>
                        <div className="inner-contentUp-banner-fourth">
                            <div className="inner-contentUp-banner-fourth-left">
                                <h2>品牌监控</h2>
                                <p>NLAP对互联网信息和用户标签及上网行为数据，准确分析品牌传播效果，深度洞察口碑影响为品牌发展提供有力支持</p>
                            </div>
                        </div>
                    </div>

                    <div style={{textAlign:'center'}}>
                        <h2>产品优势</h2>
                    </div>

                    <div style={{backgroundColor:'#fff',height:'205px'}}>
                        <div className="bottom-opw">
                            <img src="image/opw1.png"/><br/>
                            <p>接口全面</p>
                        </div>
                        <div className="bottom-opw">
                            <img src="image/opw2.png"/><br/>
                            <p>便捷易用</p>
                        </div>
                        <div className="bottom-opw">
                            <img src="image/opw3.png"/><br/>
                            <p>高效准确</p>
                        </div>
                    </div>

                </div>
                <Footer />
            </Container>
        );

    }
}

IndexApp.UIPage = page;
export default IndexApp;
