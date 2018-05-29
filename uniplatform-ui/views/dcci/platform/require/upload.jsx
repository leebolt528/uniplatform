import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,DateTimePicker,Upload,Textarea,Dialog,Loading} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '上传',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class UploadTask extends Component {
  constructor(props){
    super(props);
    this.state = {
      crawlerType : [],
      levelType : [],
      show : false
    }
    this.getFetchEnum = this.getFetchEnum.bind(this);
    this.afterSubmit = this.afterSubmit.bind( this );
    this.beforeSubmit = this.beforeSubmit.bind(this);
  }

  componentDidMount() {
    this.getFetchEnum();
  }

  getFetchEnum(){
    fetch( `${Uniplatform.context.url}/dcci/task/manage/list/enum` , {
    credentials: 'same-origin',
    method: 'POST'
    } )
      .then( ( res ) => res.json() )
      .then( ( tasks ) => {
        let crawlerType = tasks.data.crawlertype;
        let levelType = tasks.data.leveltype;
        this.setState({crawlerType,levelType});
      }).catch( ( err ) => console.log( err.toString() ) );
  }

  beforeSubmit(data){
    this.setState({ show : true });
    data.deadline = new Date(data.deadline).getTime();
    return data;
  }

  afterSubmit(data) {
    this.setState({ show : false });
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      window.open(`/dcci/platform/require/list`,'_self');
    }else{
      popup(<Dialog 
            title="错误信息" 
            message={data.message} 
          />);
    }
  }

  render() {
    let { crawlerType,levelType,show } = this.state;
    return (
        <Page>
            <SingleBanner prefix="数据中心" id={''}/>
            <Divider />
            <Row>
               <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                  <Com_Menu url={'/dcci/platform/require/list'} />
                </Col>
                <Col size={{ normal: 24, small: 24, medium: 12, large: 12 }} style={{minHeight:'600px'}}>

                  <NavigationBar code={'upload'}/>

                  <Form
                    method="pos5t"
                    action={ `${contextUrl}/dcci/task/manage/save` }
                    type="horizontal"
                    async={ true }
                    enctype="multipart/form-data"
                    onSubmit={this.beforeSubmit}
                    onAfterSubmit={this.afterSubmit}
                  >
                    <FormItem required={true}>
                      <Label>任务名:</Label>
                      <Input placeholder="请输入任务名" name="name"/>
                    </FormItem>
                    <FormItem required>
                      <Label>类型:</Label>
                      <Select value={ 'DOMESTIC_NEWS' } name="type">
                      {
                        crawlerType.map((item,indexType) => {
                          for ( var index in item ) {
                            return (<Option key={index} value={index}>{item[index]}</Option>)
                          }
                        })
                      }
                      </Select>
                    </FormItem>
                    <FormItem required>
                      <Label>优先级:</Label>
                      <Select value={ 'COMMONLY' } name="levelType">
                      {
                        levelType.map((item,indexType) => {
                          for ( var index in item ) {
                            return (<Option key={index} value={index}>{item[index]}</Option>)
                          }
                        })
                      }
                      </Select>
                    </FormItem>
                    <FormItem required>
                      <Label>截至日期:</Label>
                      <DateTimePicker placeholder="年 - 月 - 日" name="deadline" showTime={ true } showOk/>
                    </FormItem>
                    <FormItem required>
                      <Label>文件上传:</Label>
                      <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} className="upload-filed">
                        <Upload placeholder="上传excel" name="file" limit={ 1 }/>
                        <span className="file">
                          <i className="fa fa-link"></i>
                          <a href={`${contextUrl}/dcci/task/manage/example/download`} className="text"> 示例文件下载</a>
                        </span>
                      </Col>
                    </FormItem>
                    <FormItem>
                      <Label>任务描述:</Label>
                      <Textarea rows={ 3 } name="description"/>
                    </FormItem>
                    <Button type="success" active={false} htmlType="submit">提交</Button>
                  </Form>
                </Col> 
                { 
                  show ?
                  <div className="table-native-tableLoading" style={{left:0}}>
                    <div style={{position:'absolute',top:'55%',left:'47%'}}>正在提交,请稍等...</div>
                    <Loading />
                  </div>  
                  : ''
                }
              </Col>
            </Row>     
            <Footer />
        </Page>
    );
  }

}
UploadTask.UIPage = page;
export default UploadTask;