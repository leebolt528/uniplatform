import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,
    Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '文本分类-基于规则分类',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class RulesClassification extends Component {
    constructor(props){
        super(props);

        this.state = {
        }
        this.formGetter = this.formGetter.bind( this );
        this.formTirgger = this.formTirgger.bind( this );
    }

    componentDidMount() {
        this.getRules();
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }
    formGetterTemplate( getter ) {
        this.getValueTemplate = getter.value;
    }

    //重置
    formTirgger( trigger ) {
        this.reset = trigger.reset;
    }

    getRules(){
        fetch(Uniplatform.context.url + '/nlap/admin/text/classify/rules',{
            credentials: 'same-origin',
            method: 'POST'
        })
            .then((res)=>res.json())
            .then((data)=>{
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                }
                let rulesList = [];
                data.rules.map(function (item) {
                    rulesList.push({
                        text: item.templateName,
                        value: item.templateId
                    });
                });
                this.setState({rulesList});
            }).catch((err)=>console.log(err.toString()));
    }

    handleValue() {
        this.setState({classifier:undefined});
        const files = this.getValue();
        const templateId = this.getValueTemplate();
        if(!templateId){
            popup(<Snackbar message="请选择规则模板"/>);
            return;
        }else if(files.length==0){
            popup(<Snackbar message="请选择需要分类的文件"/>);
            return;
        }
        this.setState({show : true});
        let param = new FormData();
        param.append('templateId', templateId);
        files.forEach(function (file) {
            param.append("files", file);
        });
        fetch(Uniplatform.context.url + '/nlap/admin/text/classify/rule', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                    this.setState({show : false});
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                    this.setState({show : false});
                }
                let classifier = [];
                data.results.map(function (item) {
                    classifier.push({
                        fileName: item.fileName,
                        classNames: item.classNames
                    });
                });
                this.setState({classifier});
            }).catch((err) => console.log(err.toString()));
    }

    showClasses(classNames){
        let claHtml = [];
        for(let i=0;i<classNames.length;i++){
            claHtml.push(
                <span key={i} style={{backgroundColor:'#a3db5b',padding:'2px',margin:'2px'}}>
                    {classNames[i]}
                </span>
            );
        }
        return claHtml;
    }




    render() {
        let {classifier,rulesList,show} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/rulesClassification'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in8.png"/><span className="classTitle">文本分类-基于规则分类</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px',height:'100px',overflow:'auto'}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                <Label style={{margin:'4px',float:'left'}}>规则模板:</Label>
                                <Select dataSource={rulesList} showClear={false} getter={this.formGetterTemplate.bind(this)}
                                        style={{width:'20%',display:'inline-block',marginRight:'10px',float:'left'}} placeholder="请选择规则模板"/>
                                <Upload name="file" getter={this.formGetter} trigger={this.formTirgger} placeholder="选择文件"
                                        style={{display:'inline-block',float:'left'}} multiple={true}/>
                                <Button type="info" style={{margin:'0 20px'}} onClick={this.handleValue.bind(this)}>分类</Button>
                                <Button onClick={()=>{this.reset()}}>清空</Button>
                            </Col>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">分类结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    {show ? (classifier ? null : <Loading />) : null}
                                    {classifier ?
                                        <Table striped={true} multiLine={true} dataSource={classifier} bodyScrollable bodyHeight={'570px'}>
                                            <Column title="序号" scaleWidth = '20%' textAlign="center">
                                                { ( value, index ) => { return Number(index) + 1; } }
                                            </Column>
                                            <Column dataIndex="fileName" title="文件名" scaleWidth='40%' textAlign="center"/>
                                            <Column title="分类结果" scaleWidth='40%' textAlign="center">
                                                {(value)=>{
                                                    return this.showClasses(value.classNames);
                                                }}
                                            </Column>
                                        </Table>
                                        : null
                                    }
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
RulesClassification.UIPage = page;
export default RulesClassification;