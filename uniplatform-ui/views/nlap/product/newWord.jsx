import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text,Upload,
    Loading} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '新词发现',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ],
    js: [
        '../js/d3.v3.min.js',
        '../js/d3.layout.cloud.min.js',
        '../js/wordCloud.min.js'
    ]
};

class NewWord extends Component {
    constructor(props){
        super(props);

        this.state = {
            isShow:false
        }
    }

    componentDidMount() {
    }

    //文本路径检索
    pathSearch(event){
        if (event.which==13){
            let keyWord = this.getValue();
            this.setState({keyWord}, () => this.handleValue());
        }
    }
    getInput(getter){
        this.getValue=getter.value;
    }
    wordSearchAuto(data){
        if(data == ''){
            this.setState({ keyWord : '' }, () => this.handleValue());
        }
    }

    handleValue() {
        const filePath = this.getValue();
        if(!filePath.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写文本路径"/>);
            return;
        }
        this.setState({isShow : true});
        let param = new FormData();
        param.append('filePath', filePath);
        fetch(Uniplatform.context.url + '/nlap/admin/pmi/newword', {
            credentials: 'same-origin',
            method: 'POST',
            body: param
        })
            .then((res) => res.json())
            .then((data) => {
                if(data.code){
                    popup(<Snackbar message={data.message}/>);
                    this.setState({isShow : false});
                }else if (data.status != 200){
                    popup(<Snackbar message={data.msg}/>);
                    this.setState({isShow : false});
                }
                let newWords = data.result.map(function (item) {
                    let word = {
                        word : item.word,
                        score : item.score.toFixed(3)
                    }
                    return word;
                });
                let words = [],score = [];
                data.result.map(function (item) {
                    words.push(item.word);
                    score.push({
                        word : item.word,
                        score : item.score.toFixed(3)
                    });
                });
                word(words,score);
                this.setState({newWords});
            }).catch((err) => console.log(err.toString()));
    }



    render() {
        let {newWords,isShow} = this.state;
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/newWord'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in14.png"/><span className="classTitle">新词发现</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px',height:'100px'}}>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                <Label>文本路径:</Label>
                                <Input placeholder="请输入文本路径" type="search"
                                       onKeyPress={ this.pathSearch.bind(this)}
                                       getter={ this.getInput.bind(this) }
                                       onChange={this.wordSearchAuto.bind(this)}
                                       style={{width:'30%',display:'inline-block'}}>
                                    <Input.Left icon="search"/>
                                </Input>
                                <Button type="info" style={{margin:'0 20px'}} onClick={this.handleValue.bind(this)}>发现新词</Button>
                            </Col>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">发现结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }} style={{padding:'30px'}}>
                                    {newWords ?
                                        <Table striped={true} multiLine={true} dataSource={newWords} bodyScrollable>
                                            <Column title="序号" scaleWidth = '20%' textAlign="center">
                                                { ( value, index ) => { return Number(index) + 1; } }
                                            </Column>
                                            <Column dataIndex="word" title="关键词" scaleWidth='40%' textAlign="center"/>
                                            <Column dataIndex="score" title="得分" scaleWidth='40%' textAlign="center" sortable/>
                                        </Table>
                                        : null
                                    }
                                </Col>
                                <Col size={{ normal: 12, small: 24, medium: 12, large: 12 }} style={{padding:'30px',textAlign:'center'}}>
                                    <div id="chart">
                                        {isShow ? <Loading /> : null}
                                    </div>
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
NewWord.UIPage = page;
export default NewWord;