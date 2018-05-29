import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '关系抽取',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css',
        '../css/d3relation.min.css'
    ],
    js: [
        '../js/d3.v3.min.js',
        '../js/d3relation.min.js'
    ]
};

class RelationshipExtraction extends Component {
    constructor(props){
        super(props);

        this.state = {
            isShow : 'none'
        }
        this.formGetter = this.formGetter.bind( this );
    }

    componentDidMount() {
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    formGetterSource( getter ) {
        this.getValueSource = getter.value;
    }

    handleValue() {
        this.setState({relation:undefined});
        const flag = this.getValueSource();
        const text = this.getValue();
        if(!text.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要抽取的关系"/>);
            return;
        }
        this.setState({show: true});
        let param = new FormData();
        param.append('text', text);
        if(flag == 'p2p'){
            fetch(Uniplatform.context.url + '/nlap/admin/text/relation/p2p', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else if (data.status != 200){
                        popup(<Snackbar message={data.msg}/>);
                    }
                    if(data.relation.length == 0){
                        this.setState({noData:true,relation:[]});
                    }else {
                        this.setState({noData:false});
                    }
                    let relation;
                    data.relation.map(function (item) {
                        relation = item.relatios.map(function (li) {
                            let rel = {
                                source: item.nameOne,
                                target: item.nameTwo,
                                type: "suit",
                                relation: li.name + ':' + li.value.toFixed(2)
                            };
                            return rel;
                        });
                    });
                    relationship(relation);
                    let isShow = 'inline';
                    this.setState({isShow,relation});
                }).catch((err) => console.log(err.toString()));
        }else {
            fetch(Uniplatform.context.url + '/nlap/admin/text/relation/p2o', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else if (data.status != 200){
                        popup(<Snackbar message={data.msg}/>);
                    }
                    if(data.relation.length == 0){
                        this.setState({noData:true,relation:[]});
                    }else {
                        this.setState({noData:false});
                    }
                    let relation;
                    data.relation.map(function (item) {
                        relation = item.relatios.map(function (li) {
                            let rel = {
                                source: item.nameOne,
                                target: item.nameTwo,
                                type: "suit",
                                relation: li.name + ':' + li.value.toFixed(2)
                            };
                            return rel;
                        });
                    });
                    relationship(relation);
                    let isShow = 'inline';
                    this.setState({isShow,relation});
                }).catch((err) => console.log(err.toString()));
        }

    }




    render() {
        let {isShow,relation,noData,show} = this.state;
        const contents = '曾经上过春晚的相声演员李丁跟搭档董建春实力强劲，二人一路过关斩将，杀入了总决赛。';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/relationshipExtraction'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in17.png"/><span className="classTitle">关系抽取</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px'}}>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents}/>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>抽取</Button>
                            <Select value="p2p" showClear={false} getter={ this.formGetterSource.bind(this) }
                                    style={{float:'right',width:'20%',display:'inline-block',marginRight:'10px'}}>
                                <Option value="p2p">人-人</Option>
                                <Option value="p2o">人-机构</Option>
                            </Select>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">抽取结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{textAlign:'center',margin:'20px 23px',width:'95%'}}>
                                    {noData ? <span>无结果</span> :
                                        <div id="chart" style={{display:isShow}}>
                                        </div>
                                    }
                                    {show ? (relation ? null : <Loading />) : null}
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
RelationshipExtraction.UIPage = page;
export default RelationshipExtraction;