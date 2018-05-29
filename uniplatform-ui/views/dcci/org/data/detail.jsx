import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text,Card,CardBody,Image,Pagination,Tabs,Tab,Table,Column} from 'epm-ui';
import { COMM_HeadBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '产品详情',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css',
      '../../css/index.min.css',
      '../../css/productDetail.min.css'
    ],
    js: [
        '../../js/ace.min.js',
        '../../js/mode-json.min.js'
    ]
};

const contextUrl = '/uniplatform';

class ProductDetail extends Component {
  constructor(props){
    super(props);
    this.state = {
      singleApi : [],
      apiAll : [],
      units : [],
      singleDetail : [],
      codeList : [],
      apiDetail : [],
      requestParms : [],
      requestExample : [],
      returnParms : [],
      returnExample : []
    }
    this.apiEditorData = this.apiEditorData.bind(this);
    this.getFetchApiAll = this.getFetchApiAll.bind(this);
    this.getFetchApiMnum = this.getFetchApiMnum.bind(this);
  }

  componentDidMount() {
    this.apiEditorData();
    this.getFetchApiAll();
    this.getFetchApiMnum();
  }

  apiEditorData(){
    fetch( `${Uniplatform.context.url}/dcci/api/get/${this.props.page.id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let singleApi = apiData.data;
          this.setState({singleApi});
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  getFetchApiAll(){
    fetch( `${Uniplatform.context.url}/dcci/api/detail/list/all/?apiId=${this.props.page.id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiAll = apiData.data;
          if(apiAll.length == 0){
            this.setState({ apiAll });
          }else{
            this.setState({apiAll}, ()=> this.getApiDetai('0',apiAll[0].id));
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  getFetchApiMnum(){
    fetch( `${Uniplatform.context.url}/dcci/api/list/enum` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let units = apiData.data.unit;
          this.setState({units});
        }).catch( ( err ) => console.log( err.toString() ) );
  }
  getSpeUnit(singleApi,units){
    let result = '';
    let unit = singleApi.unit;
    let specif = singleApi.specifications;
    units.map((item,indexType) => {
      for ( var index in item ) {
        if(index == unit){
          result = `${specif} / ${item[unit]}`;
        }
      }
    });
    return result;
  }
  getApiDetai(index,id){
    let _$thisId = $('#apibleft ul li');
    _$thisId.find('a').removeClass('on');
    _$thisId.eq(index).find('a').addClass("on");
    this.apiDetailData(id);
  }

  apiDetailData(id){
    fetch( `${Uniplatform.context.url}/dcci/api/detail/list/all/${id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiDetail = apiData.data.apiDetail;
          let requestParms = apiData.data.requestParms;
          let requestExample = apiData.data.requestExample;
          let returnParms = apiData.data.returnParms;
          let returnExample = apiData.data.returnExample;
          this.setState({apiDetail,requestParms,returnParms});

          this.setState({requestExample} ,() => this.editGetExample(requestExample));
          this.setState({returnExample} ,() => this.editBackExample(returnExample));

        }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchCodeList(){
    fetch( `${Uniplatform.context.url}/dcci/api/code/list/all` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let codeList = apiData.data;
          this.setState({codeList});
          if (codeList.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
          }
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  editGetExample(getExample){
    //初始化对象
    if(getExample !=null && getExample.hasOwnProperty("code")){
      getExample = getExample.code;
    }
    let editor = ace.edit("getExampleDiv");
    //设置语言
    let language = "json";
    editor.session.setMode("ace/mode/" + language);
    //设置编辑器中的值
    editor.setValue(getExample);
    //字体大小
    editor.setFontSize(16);
    //设置只读（true时只读，用于展示代码）
    editor.setReadOnly(true);
    //自动换行,设置为off关闭
    editor.setOption("wrap", "free");
  }

  editBackExample(backExample){
    //初始化对象
    if(backExample !=null && backExample.hasOwnProperty("example")){
      backExample = backExample.example;
    }
    let editor = ace.edit("backExampleDiv");
    //设置语言
    let language = "json";
    editor.session.setMode("ace/mode/" + language);
    //设置编辑器中的值
    editor.setValue(backExample);
    //字体大小
    editor.setFontSize(16);
    //设置只读（true时只读，用于展示代码）
    editor.setReadOnly(true);
    //自动换行,设置为off关闭
    editor.setOption("wrap", "free");
  }

  render() {
    const { singleApi,apiAll,units,codeList,apiDetail,requestParms,requestExample,returnParms,returnExample } = this.state;
    return (
        <Page>
          <COMM_HeadBanner prefix="数据中心"/>
          <Divider />
          <Row>
            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{minHeight:'600px'}}>
              <Row>
                <Col size={{ normal: 24, small: 24, medium: 3, large: 3 }}></Col>
                <Col size={{ normal: 24, small: 24, medium: 5, large: 5 }}>
                  <Card style={{height: '230px'}}>
                    <CardBody>
                      <Image src={ `${ contextUrl }/dcci/api/image/${singleApi.imageName}` } alt="加载图片失败" style={{height: '200px'}}></Image>
                    </CardBody>
                  </Card>
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }}>
                  <h3>{singleApi.name}</h3>
                  <Text>{singleApi.description}</Text>
                  <Divider />
                  <Text>价格 : {singleApi.price}</Text>
                  <Divider />
                  <Text>规格 : {this.getSpeUnit(singleApi,units)}</Text>
                  <Divider />
                </Col>
              </Row>

              <Divider/>
              <Tabs align="scoped">
                <Tab title="API">
                  <Row>
                    <Col size={{ normal: 24, small: 24, medium: 5, large: 5 }}>
                      <div id="apibleft">
                        <ul>
                        {
                          apiAll.map((item,index) => {
                            return (
                              <li key={index}><a onClick={this.getApiDetai.bind(this,index,item.id)}>{item.name}</a></li>
                            )
                          })
                        }
                        </ul>
                      </div>
                    </Col>
                    <Col size={{ normal: 24, small: 24, medium: 19, large: 19 }}>
                    {
                      apiAll.length != 0 ? 
                        <div id="apibright">
                          <div className="apiline">
                            <Text>接口地址 : {apiDetail.url}</Text>
                          </div>
                          <div className="apiline">
                            <Text>请求方式 : {apiDetail.method}</Text>
                          </div>
                          <div className="apiline">
                            <Text>请求示例 : {apiDetail.sample}</Text>
                          </div>
                          <div className="apilinebox">
                            <h4>请求参数 :</h4>
                            <Table dataSource={requestParms} striped={ true } complex headBolder={ true }>
                              <Column dataIndex="name" title="名称" textAlign="center" scaleWidth="15%"/>
                              <Column dataIndex="type" title="类型" textAlign="center" scaleWidth="10%"/>
                              <Column dataIndex="example" title="示例" textAlign="center" scaleWidth="25%"/>
                              <Column title="必填" textAlign="center" scaleWidth="10%">
                              { ( value ) => { 
                                return (value.require == '0' ? '否' : '是')
                                }    
                              }
                              </Column>
                              <Column dataIndex="description" title="描述" textAlign="center" scaleWidth="40%"/>
                            </Table>
                          </div>
                          <div className="apilinebox">
                            <h4>返回参数 :</h4>
                            <Table dataSource={returnParms} striped={ true } complex headBolder={ true }>
                              <Column dataIndex="name" title="名称" textAlign="center" scaleWidth="15%"/>
                              <Column dataIndex="type" title="类型" textAlign="center" scaleWidth="10%"/>
                              <Column dataIndex="example" title="示例" textAlign="center" scaleWidth="25%"/>
                              <Column dataIndex="description" title="描述" textAlign="center" scaleWidth="50%"/>
                            </Table>
                          </div>
                          <div className="apilinebox">
                            <h4>请求示例 :</h4>
                            <div id="getExampleDiv" style={{minHeight:'500px'}}>
                              <Textarea rows={25}></Textarea>
                            </div>
                          </div>
                          <div className="apilinebox">
                            <h4>返回示例 :</h4>
                            <div id="backExampleDiv" style={{minHeight:'500px'}}>
                              <Textarea rows={25}></Textarea>
                            </div>
                          </div>
                        </div>
                      : '无记录'
                    }
                    </Col>
                  </Row>
                </Tab>
                <Tab title="状态码参照" onClick={this.getFetchCodeList.bind(this)}>
                  <Table dataSource={codeList} striped={ true } complex headBolder={ true }>
                    <Column dataIndex="code" title="状态码" textAlign="center" scaleWidth="10%"/>
                    <Column dataIndex="explain" title="状态码说明" textAlign="center" scaleWidth="90%"/>
                  </Table>
                </Tab>
              </Tabs>

            </Col>
          </Row>     
        <Footer />
      </Page>
    );
  }

}
ProductDetail.UIPage = page;
export default ProductDetail;