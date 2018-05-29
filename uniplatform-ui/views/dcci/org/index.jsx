import React, { Component } from 'react';
import { COMM_HeadBanner, Footer } from '../../../components/uniplatform-ui';
import { Container,Icon,Carousel,Button } from 'epm-ui';

const page = {
  title: '首页',
  css: [
    '../css/index.min.css',
      '../css/owl.carousel.min.css'
  ],
    js:[
        '../js/owl.carousel.min.js?v=beta.1.8',
        '../js/index.min.js'
    ]
};

class IndexApp extends Component {

  getProdect(){
    window.open(`/dcci/org/data/list`,'_self');
  }

  getProdectList(){
    window.open(`/dcci/platform/index`,'_self');
  }

  render() {
    return (
      <Container type="fluid">
        <COMM_HeadBanner prefix="非结构化统一平台"/>

        <div className="inner-content">
          <div className="inner-contentUp">
            <div className="inner-contentUp-top">
              <div className="inner-contentUp-topL">
                <Icon style={{color:'#00c2de'}} icon="line-chart" className="fa fa-camera-retro fa-2x"/>
                <h2 style={{display:'inline'}}>      数据中心</h2>
                <p>互联网数据中心依托云计算中心强大的软硬件支持能力，实现跨地域分布式采集，集中式加工存储，对外提供多方式数据推送和多领域数据产品服务。</p>
                <Button className="btn" style={{backgroundColor:'#02aab5',color:'white'}} onClick={this.getProdectList.bind(this)}>
                  管控平台
                </Button>
                <Button className="btn" style={{backgroundColor:'#02aab5',color:'white'}} onClick={this.getProdect.bind(this)}>
                  数据商城
                </Button>
              </div>
              <div className="inner-contentUp-topR">
                <img className="inner-contentUp-topL-normal" src="../image/sjzx.png" />
              </div>
            </div>
            <div className="inner-contentUp-bottom">
              <h2>服务优势</h2>
              <div className="inner-contentUp-bottom-dec">
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <Icon icon="pie-chart" className="fa fa-camera-retro fa-4x"/>
                    {/*<img alt="icon" src="image/yuqing.png"/>*/}
                    <h4>数据丰富</h4>
                  </a>
                  <span>独特的数据采集模式，深度整合各行业数据，沉淀交叉领域价值数据。</span>
                </div>
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <Icon icon="dashboard" className="fa fa-camera-retro fa-4x"/>
                    {/*<img alt="icon" src="image/hulian.png"/>*/}
                    <h4>一键接入</h4>
                  </a>
                  <span>数据产品接入方式便捷高效，支持一键接入。</span>
                </div>
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <Icon icon="database" className="fa fa-camera-retro fa-4x"/>
                    {/*<img alt="icon" src="image/kefu.png"/>*/}
                    <h4>接入方式多样</h4>
                  </a>
                  <span>为数据需求方提供数据包、API接口等多种便捷数据接入方式。</span>
                </div>
                <div className="inner-contentUp-bottom-decLink">
                  <a href="">
                    <Icon icon="archive" className="fa fa-camera-retro fa-4x"/>
                    {/*<img alt="icon" src="image/zhengfu.png"/>*/}
                    <h4>个性定制</h4>
                  </a>
                  <span>爬虫动态提取互联网大数据，可为用户个性化定制所需数据包。</span>
                </div>
              </div>
            </div>
          </div>



          <div className="margin-top">
            <div className="row">
              <div className="large-12 columns">
                <div className="owl-carousel">
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/technology.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>科技</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/education.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>教育</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/medical.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>医疗</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/pe.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>体育</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/technology.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>科技</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/technology.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>科技</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/technology.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>科技</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                  <div className="inner-contentDown">
                    <ul>
                      <li className="owl-lazy" style={{width:'100%'}}>
                        <a href="">
                          <img src="../image/technology.png"/>
                          <div className="inner-contentDown-mask">
                            <div className="inner-contentDown-bg"></div>
                            <div className="inner-contentDown-content">
                              <h4>科技</h4>
                              <p>行业词库、标签体系、精选语料、高效算法、行业词库、标签体系、精选语料、高效算法</p>
                            </div>
                          </div>
                        </a>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>


          <div style={{textAlign:'center',margin:'40px'}}>
            <h2 className="inner-contentUp-bottom-subtitle">精准定制，基于场景的行业解决方案</h2>
            <h3 className="inner-contentUp-bottom-subtitle">零售、金融、能源、健康、制造、传媒，深入各行业满足个性化需求</h3>
            <h3 className="inner-contentUp-bottom-subtitle">零售、金融、能源、健康、制造、传媒，深入各行业满足个性化需求</h3>
          </div>

        </div>
        <Footer />
      </Container>
    );

  }
}

IndexApp.UIPage = page;
export default IndexApp;
