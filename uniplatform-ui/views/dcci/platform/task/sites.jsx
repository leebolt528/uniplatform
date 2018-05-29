import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Pagination} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '任务管理-查看采集点',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

class CurrentSiteList extends Component {
  constructor(props){
    super(props);

    this.state = {
      index:1,
      size:20,
      total:0,
      taskRelation : this.props.page.taskRelation,
      siteList : [],
      urlsitetype : []
    }
    this.getFetchSiteList = this.getFetchSiteList.bind(this);
    this.getFetchEnum = this.getFetchEnum.bind(this);
   
  }

  componentDidMount() {
    this.getFetchEnum();
    this.getFetchSiteList();
  }

  getFetchEnum(){
    fetch( `${Uniplatform.context.url}/dcci/task/site/list/enum/url` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        let urlsitetype = tasks.data.urlsitetype;
        this.setState({urlsitetype});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  getFetchSiteList(){
    let param = new FormData();
    let { size,index,taskRelation } = this.state;
    param.append( 'size', size );
    param.append( 'number', index);
    param.append( 'taskRelation', taskRelation );
    fetch( `${Uniplatform.context.url}/dcci/task/site/task/list` , {
    credentials: 'same-origin',
    method: 'POST',
    body : param } )
      .then( ( res ) => res.json() )
      .then( ( sites ) => {
        let siteList = sites.data.content;
        let total = sites.data.totalElements;
        this.setState({siteList,total});
        if(siteList.length <= 0) {
          popup(<Snackbar message="抱歉，暂时没有数据"/>);
        }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchSiteList());
  }

  downSite(){
    let {siteList,taskRelation} = this.state;
    let countSite = siteList.length;
    if(countSite > 5000){
      popup( <Snackbar message={`导出数据量应小于5K`} /> );
      return false;
    }else if(countSite == 0){
      popup( <Snackbar message={`没有要导出的数据`} /> );
      return false;
    }
    window.open(`${Uniplatform.context.url}/dcci/task/site/task/download?taskRelation=${taskRelation}`,'_self');
  }

  getType(data){
    let result = '';
    let urlsitetype = this.state.urlsitetype;
    urlsitetype.map((item,indexType) => {
      for ( var index in item ) {
        if(index == data){
          result = item[data];
        }
      }
    });
    return result;
  }

  render() {
    let {siteList,index,size,total,urlsitetype} = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/task/list'} />
                </Col>

                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>
                  <NavigationBar code={'currentSiteList'} innerId={this.state.taskRelation} />

                  <Button type="default" size="small" style={{position:'relative',zIndex:1,marginBottom: '10px'}} onClick={this.downSite.bind(this)}>下载配置</Button>

                  <Table dataSource={siteList} striped={ true } complex headBolder={ true }>
                    <Column dataIndex="name" title="站点名" textAlign="center" scaleWidth="20%"/>
                    <Column dataIndex="board" title="板块" textAlign="center" scaleWidth="20%"/>
                    <Column dataIndex="url" title="url" textAlign="center" scaleWidth="45%"/>
                    <Column title="状态" textAlign="center" scaleWidth="15%">
                    { ( value ) => {
                          let result = this.getType(value.status);
                          return result;
                      } }
                    </Column>
                  </Table>
                  <Divider/>
                  <Pagination align='right' index={index} total={total} size={size}
                          onChange={this.exChangePagi.bind(this)}/>
                  <Divider/>
                </Col>    
              </Col>
            </Row>     
            <Footer />
        </Page>
    );
  }

}
CurrentSiteList.UIPage = page;
export default CurrentSiteList;