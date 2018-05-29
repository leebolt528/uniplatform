import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Pagination} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '站点管理',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

class SiteManage extends Component {
  constructor(props){
    super(props);

    this.state = {
      index:1,
      size:20,
      total:0,
      name : '',
      url : '',
      status : '',
      siteList : [],
      downSel : []
    }
    this.getFetchSiteList = this.getFetchSiteList.bind(this);
    this.beforeSubmit = this.beforeSubmit.bind(this);
    this.handleSelect = this.handleSelect.bind(this);
  }

  componentDidMount() {
    this.getFetchSiteList();
  }

  getFetchSiteList(){
    let param = new FormData();
    let { size,name,url,status,index } = this.state;
    param.append( 'size', size );
    param.append( 'name', name );
    param.append( 'number', index);
    param.append( 'url', url );
    param.append( 'status', status );
    fetch( `${Uniplatform.context.url}/dcci/site/list` , {
    credentials: 'same-origin',
    method: 'POST',
    body : param
    } )
      .then( ( res ) => res.json() )
      .then( ( sites ) => {
        let siteList = sites.data.content;
        let total = sites.data.totalElements;
        this.setState({siteList,total});
        if (sites.data.content.length <= 0){
          popup(<Snackbar message="抱歉，暂时没有数据"/>);
        }
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  beforeSubmit(data){
    let status = data.status;
    let url = data.url;
    let name = data.name;
    this.setState({ status,url,name } , () => this.getFetchSiteList() );
  }

  downloadAll(){
    let {siteList,status,url,name} = this.state;
    let countSite = siteList.length;
    if(countSite > 5000){
      popup( <Snackbar message={`导出数据量应小于5K`} /> );
      return false;
    }else if(countSite == 0){
      popup( <Snackbar message={`没有要导出的数据`} /> );
      return false;
    }
    window.open(`${Uniplatform.context.url}/dcci/site/export/excel/list?status=${status}&url=${url}&name=${name}`,'_self');
  }

   handleSelect(data){
    let downSel = [];
    downSel = Object.keys(data).map((index) => 
        `${data[index].id}`
    ).join(',');
    this.setState({downSel});
  }

  downloadSel(){
    let downSel = this.state.downSel;
    if(downSel == ""){
      popup( <Snackbar message={`没有选择要导出的数据`} /> );
      return false; 
    }
    window.open(`${Uniplatform.context.url}/dcci/site/export/excel?ids=${downSel}`,'_self');
  }

  downJsonAll(){
    let {siteList,status,url,name} = this.state;
    let countSite = siteList.length;
    if(countSite > 5000){
      popup( <Snackbar message={`生成配置应小于5K`} /> );
      return false;
    }else if(countSite == 0){
      popup( <Snackbar message={`没有要生成的配置`} /> );
      return false;
    }
    window.open(`${Uniplatform.context.url}/dcci/site/export/configure/list?status=${status}&url=${url}&name=${name}`,'_self');
  }

  downJsonSel(){
    let downSel = this.state.downSel;
    if(downSel == ""){
      popup( <Snackbar message={`没有选择要生成的配置`} /> );
      return false; 
    }
    window.open(`${Uniplatform.context.url}/dcci/site/export/configure?ids=${downSel}`,'_self');
  }

  deleteSite(id) {
    const confirmShow = confirm("确定要删除该条数据?");
    if (!confirmShow) {
      return false;
    }
    fetch(`${Uniplatform.context.url}/dcci/site/delete/${id}`, {
      credentials: 'same-origin',
      method: 'POST'
    })
      .then((res) => res.json())
      .then((data) => {
        if(data.code == '1'){
          this.getFetchSiteList();
          popup(<Snackbar message={data.message}/>)
        }else{
          popup(<Snackbar message={data.message}/>)
        }
      }).catch((err) => console.log(err.toString()));
  }
  
  exChangePagi(index, size) {
    this.setState({index, size}, () => this.getFetchSiteList());
  }

  render() {
    let {siteList,index,size,total} = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/site/list'}/>
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

                  <NavigationBar code={'siteManage'} />

                  <Form type="inline"
                        action="fack" onSubmit={ this.beforeSubmit }>
                    <FormItem>
                      <Label>url:</Label>
                      <Input  name="url" placeholder="请输入url"/>
                    </FormItem>
                    <FormItem>
                      <Label>站点名:</Label>
                      <Input  name="name" placeholder="请输入站点名"/>
                    </FormItem>
                    <FormItem>
                      <Label>采集状态:</Label>
                      <Select value={ '1' } name="status">
                        <Option value="1">采集</Option>
                        <Option value="2">全部</Option>
                      </Select>
                    </FormItem>
                    <Button htmlType="submit" type="default">查询</Button>
                  </Form>

                  <ButtonGroup style={{position:'relative',zIndex:1}}>
                    <Button type="default" size="small" onClick={this.downloadAll.bind(this)}>导出全部</Button>
                    <Button type="default" size="small" onClick={this.downloadSel.bind(this)}>导出选中</Button>
                    <Button type="default" size="small" onClick={this.downJsonAll.bind(this)}>生成全部配置</Button>
                    <Button type="default" size="small" onClick={this.downJsonSel.bind(this)}>生成选中配置</Button>
                  </ButtonGroup>
                  <Divider/>
                    
                  <Table dataSource={siteList} striped={ true } headBolder={ true } checkable onCheck={ this.handleSelect } complex>
                    <Column dataIndex="name" title="站点名" textAlign="center" scaleWidth="15%"/>
                    <Column dataIndex="board" title="板块" textAlign="center" scaleWidth="15%"/>
                    <Column dataIndex="category" title="频道" textAlign="center" scaleWidth="15%"/>
                    <Column dataIndex="url" title="url" textAlign="center" scaleWidth="45%"/>
                    <Column title="操作" textAlign="center" scaleWidth="10%">
                        { ( value ) => {
                            return (
                              <p>
                                  <a onClick={this.deleteSite.bind(this,value.id)}>删除</a>
                              </p>
                            )
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
SiteManage.UIPage = page;
export default SiteManage;