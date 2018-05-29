import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '商城添加',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class ApiAdd extends Component {
  constructor(props){
    super(props);
    this.state = {
      apiType : [],
      units : [],
      image : ''
    }
    this.afterApiAddSub = this.afterApiAddSub.bind(this);
    this.getFetchApiMnum = this.getFetchApiMnum.bind(this);
    this.beforeApiAddSub = this.beforeApiAddSub.bind(this);
  }

  componentDidMount() {
    this.getFetchApiMnum();
  }

  getFetchApiMnum(){
    fetch( `${Uniplatform.context.url}/dcci/api/list/enum` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let apiType = apiData.data.apitype;
          let units = apiData.data.unit;
          this.setState({apiType,units});
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  afterApiAddSub(data){
    if(data.code == '1'){
      popup(<Snackbar message={data.message}/>);
      window.open(`/dcci/platform/data/list`,'_self');
    }else{
      this.showError(data);
    }
  }

  showError(data){
    popup(<Dialog 
          title="错误信息" 
          message={data.message} 
        />);
  }

  changeImg(option,data){

    var _this = $("input#fileUpload")[0];

    //获取上传文件的数量
    var countFiles = _this.files.length;
  
    var imgPath = _this.value;
    var extn = imgPath.substring(imgPath.lastIndexOf('.') + 1).toLowerCase();
    var image_holder = $("#image-holder");
    image_holder.empty();
  
    if (extn == "gif" || extn == "png" || extn == "jpg" || extn == "jpeg") {
      if (typeof (FileReader) != "undefined") {

        let image = _this.files[0];
        this.setState({ image });

        var reader = new FileReader();
        reader.onload = function (e) {
            $("<img />", {
                "src": e.target.result,
                "class": "thumb-image"
            }).appendTo(image_holder);
 
        }
        image_holder.show();
        reader.readAsDataURL(image);

      } else {
        popup(<Dialog title="错误信息" message={'你的浏览器不支持FileReader！'} />);
      }
    } else {
      popup(<Dialog title="错误信息" message={'请选择图像文件'} />);
    }
  }

  beforeApiAddSub(data){
    data["image"] = this.state.image;
    return data;
  }

  render() {
    let {apiType,units} = this.state;
    return (
        <Page>
          <SingleBanner prefix="数据中心" id={''}/>
          <Divider />
          <Row>
              <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
              <Col size={{ normal: 24, small: 24, medium: 4, large: 4 }}>
                <Com_Menu url={'/dcci/platform/data/list'} />
              </Col>
              <Col size={{ normal: 24, small: 24, medium: 20, large: 20 }} style={{minHeight:'600px'}}>

              <NavigationBar code={'apiAdd'} />

              <Form type="horizontal"
                method="post"
                action={`${contextUrl}/dcci/api/save`}
                async={true}
                enctype="multipart/form-data"
                onSubmit={this.beforeApiAddSub }
                onAfterSubmit={this.afterApiAddSub }
                >
                  <FormItem required>
                    <Label>名称</Label>
                    <Input name="name" placeholder="请输入名称"/>
                  </FormItem>
                  <FormItem>
                    <Label>数据分类</Label>
                    <Select value={ 'SOCIAL' } name="type">
                    {
                      apiType.map((item,indexType) => {
                        for ( var index in item ) {
                          return (<Option key={index} value={index}>{item[index]}</Option>)
                        }
                      })
                    }
                    </Select>
                  </FormItem>
                  <FormItem required>
                    <Label>上传图片:</Label>
                    <div className="epm field" style={{padding: '1em', border: '1px solid #e2e2e2'}}>       
                      <input id="fileUpload" type="file" name="image" onChange={this.changeImg.bind(this,'image')}/><br />
                      <div id="image-holder"> </div>
                    </div>
                  </FormItem>
                  <FormItem required>
                    <Label>价格</Label>
                    <Input name="price" placeholder="请输入价格" pattern= { /^[0-9]*$/ }/>
                  </FormItem>
                  <FormItem required>
                    <Label>规格</Label>
                    <Input name="specifications" placeholder="请输入规格" pattern= { /^[0-9]*$/ }/>
                  </FormItem>
                  <FormItem>
                    <Label>单位</Label>
                    <Select value={ 'FREQUENCY' } name="unit">
                    {
                      units.map((item,indexType) => {
                        for ( var index in item ) {
                          return (<Option key={index} value={index}>{item[index]}</Option>)
                        }
                      })
                    }
                    </Select>
                  </FormItem>
                  <FormItem>
                    <Label>描述</Label>
                    <Textarea name="description" rows={ 3 } />
                  </FormItem>
                  <Button type="success" active={false} htmlType="submit">提交</Button>
                </Form>
              </Col>    
            </Col>
          </Row>     
        <Footer />
      </Page>
    );
  }

}
ApiAdd.UIPage = page;
export default ApiAdd;