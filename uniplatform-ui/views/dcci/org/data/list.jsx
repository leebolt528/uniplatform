import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text,Card,CardBody,Image,Pagination} from 'epm-ui';
import { COMM_HeadBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '数据商城',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css',
      '../../css/index.min.css'
    ]
};

const contextUrl = '/uniplatform';

class ProductList extends Component {
  constructor(props){
    super(props);
    this.state = {
      index:1,
      size:12,
      total:0,
      type: '',
      apiList : [],
      apiType : []
    }
    this.getFetchApiList = this.getFetchApiList.bind(this);
    this.getFetchApiMnum = this.getFetchApiMnum.bind(this);
  }

  componentDidMount() {
    this.getFetchApiList();
    this.getFetchApiMnum();
  }

  getFetchApiList(){
    let param = new FormData();
    let { size,type,index } = this.state;
    param.append( 'size', size );
    param.append( 'number', index);
    param.append( 'status', 'OPEN' );
    param.append( 'type', type );
    fetch( `${Uniplatform.context.url}/dcci/api/list` , {
      credentials: 'same-origin',
      method: 'POST',
      body: param
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiList = apiData.data.content;
          let total = apiData.data.totalElements;
          this.setState({apiList,total});
          if (apiList.length <= 0){
            popup(<Snackbar message="抱歉，暂时没有数据"/>);
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
          let apiType = apiData.data.apitype;
          this.setState({apiType});
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchApiList());
  }

  getType(data){
    let type = data;
    this.setState({type}, () => this.getFetchApiList());
  }

  getDetail(id){
    window.open(`/dcci/org/data/detail?id=${id}`,'_self');
  }

  render() {
    let {apiList,index,size,total,apiType} = this.state;
    return (
        <Page>
          <COMM_HeadBanner prefix="数据中心"/>
          <Divider />
          <Row>
            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{minHeight:'600px'}}>
              <Row>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }} style={{textAlign:'right'}}>数据分类 ：</Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }}>
                  <span onClick={this.getType.bind(this,'ALL')} className="bgColor">
                    <Col style={{marginBottom:'10px'}} size={{ normal: 24, small: 24, medium: 2, large: 2 }} >全部</Col>
                  </span>
                {
                  apiType.map((item,indexType) => {
                    for ( var index in item ) {
                      return (
                        <span key={index} onClick={this.getType.bind(this,index)} className="bgColor">
                          <Col style={{marginBottom:'10px'}} size={{ normal: 24, small: 24, medium: 2, large: 2 }} >{item[index]}</Col>
                        </span>
                        )
                    }
                  })
                }
                    
                </Col>
              </Row>
              <Divider line />

              <Row>
                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                {
                  apiList.map((item,index) => {
                    return( 
                      <span key={index} className="bgCard" onClick={this.getDetail.bind(this,item.id)}>
                      <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }} style={{marginBottom:'15px'}}>
                        <Card style={{height: '280px'}}>
                          <CardBody>
                            <Image src={ `${ contextUrl }/dcci/api/image/${item.imageName}` } alt="加载图片失败" style={{height: '150px'}}></Image>
                            <h3>{item.name}</h3>
                            <p className="descOver">{item.description}</p>
                          </CardBody>
                        </Card>
                      </Col>
                      </span>
                    )
                  })
                }
                </Col>
              </Row>
              <Divider/>
              <Pagination align='right' index={index} total={total} size={size} onChange={this.exChangePagi.bind(this)}/>
            </Col>
          </Row>   
        <Divider/>  
        <Footer />
      </Page>
    );
  }

}
ProductList.UIPage = page;
export default ProductList;