import React, { Component } from 'react';
import {Page,Row,Col,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,Snackbar,popup,Upload,Textarea,Dialog,Text} from 'epm-ui';
import { SingleBanner,Footer ,Com_Menu,NavigationBar} from '../../../../components/uniplatform-ui';

const page = {
    title: '商城修改',
    css: [
      '../../css/singleBanner.min.css',
      '../../css/siteManager.min.css'
    ]
};

const contextUrl = '/uniplatform';

class ApiUpdate extends Component {
  constructor(props){
    super(props);
    this.state = {
      apiType : [],
      units : [],
      singleApi : [],
      image : '',
      imageName : ''
    }
    this.apiEditorData = this.apiEditorData.bind(this);
    this.getFetchApiMnum = this.getFetchApiMnum.bind(this);
    this.afterApiUpdateSub = this.afterApiUpdateSub.bind(this);
    this.beforeApiUpdateSub = this.beforeApiUpdateSub.bind(this);
  }

  componentDidMount() {
    this.getFetchApiMnum();
    this.apiEditorData();
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

  apiEditorData(){
    fetch( `${Uniplatform.context.url}/dcci/api/get/${this.props.page.id}` , {
      credentials: 'same-origin',
      method: 'POST'
      } )
        .then( ( res ) => res.json() )
        .then( ( apiData ) => {
          let singleApi = apiData.data;
          let imageName = apiData.data.imageName;
          this.setState({singleApi,imageName},() => this.getImage());
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  getImage(){
    fetch( `${Uniplatform.context.url}/dcci/api/image/${this.state.imageName}` , {
      credentials: 'same-origin',
      method: 'GET'
      } )
        .then( ( apiData ) => {
          var image_holder = $("#image-holder");
          $(`<img src=${apiData.url} class="thumb-image"/>`).appendTo(image_holder);
          image_holder.show();
        }).catch( ( err ) => console.log( err.toString() ) );
  }

  beforeApiUpdateSub(data) {
    data["image"] = this.state.image;
    data["id"] = this.state.singleApi.id;
    data["status"] = this.state.singleApi.status;
    return data;
  }

  afterApiUpdateSub(data){
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
    let imgPath = _this.value;

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

  render() {
    let {apiType,units,singleApi,imageName} = this.state;
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

              <NavigationBar code={'apiUpdate'} innerId={this.props.page.id}/>

              <Form type="horizontal"
                method="post"
                action={`${contextUrl}/dcci/api/update`}
                async={true}
                enctype="multipart/form-data"
                onSubmit={this.beforeApiUpdateSub }
                onAfterSubmit={this.afterApiUpdateSub }
                >
                  <FormItem required>
                    <Label>名称</Label>
                    <Input name="name" value={singleApi ? singleApi.name : null}/>
                  </FormItem>
                  <FormItem>
                    <Label>数据分类</Label>
                    <Select name="type" value={singleApi ? singleApi.type : null}>
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
                    <Input name="price" value={singleApi ? singleApi.price : null} pattern= { /^[0-9]*$/ }/>
                  </FormItem>
                  <FormItem required>
                    <Label>规格</Label>
                    <Input name="specifications" value={singleApi ? singleApi.specifications : null} pattern= { /^[0-9]*$/ }/>
                  </FormItem>
                  <FormItem>
                    <Label>单位</Label>
                    <Select name="unit" value={singleApi ? singleApi.unit : null}>
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
                    <Textarea name="description" rows={ 3 } value={singleApi ? singleApi.description : null}/>
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
ApiUpdate.UIPage = page;
export default ApiUpdate;