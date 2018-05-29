import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Form,Select,Label,Input,FormItem,Option,ButtonGroup,
    Modal,ModalHeader,ModalBody,ModalFooter,Textarea,popup,Snackbar,Pagination,DateTimePicker,Text} from 'epm-ui';
import { Footer,Com_Menu,Chart,convertByteToGbUtil,Chartbar,COMM_HeadBanner,ProductMenu} from '../../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: '地址匹配',
    css: [
        '../css/index.min.css',
        '../css/leftnav.min.css',
        '../css/product.min.css'
    ]
};

class AddressMatch extends Component {
    constructor(props){
        super(props);

        this.state = {
            isExcel: false
        }
        this.formGetter = this.formGetter.bind( this );
    }

    componentDidMount() {
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    formGetterOne( getter ) {
        this.getValueOne = getter.value;
    }
    formGetterTwo( getter ) {
        this.getValueTwo = getter.value;
    }

    formGetterSource( getter ) {
        this.getValueSource = getter.value;
    }

    handleChange(value){
        let {isExcel} = this.state;
        if(value == 'Excel'){
            isExcel = true;
        }else {
            isExcel = false;
        }
        this.setState({isExcel});
    }

    improt(){
        const flag = this.getValueSource();
        const one = this.getValueOne();
        const two = this.getValueTwo();
        if(flag == 'DB'){
            let param = new FormData();
            param.append('strName', one);
            param.append('strColumn', two);
            fetch(Uniplatform.context.url + '/nlap/admin/address/match/from/mysql', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else {
                        popup(<Snackbar message={data.msg}/>);
                    }
                }).catch((err) => console.log(err.toString()));
        }else if(flag == 'Excel'){
            let param = new FormData();
            param.append('path', one);
            param.append('column', two);
            fetch(Uniplatform.context.url + '/nlap/admin/address/match/from/excel', {
                credentials: 'same-origin',
                method: 'POST',
                body: param
            })
                .then((res) => res.json())
                .then((data) => {
                    if(data.code){
                        popup(<Snackbar message={data.message}/>);
                    }else {
                        popup(<Snackbar message={data.msg}/>);
                    }
                }).catch((err) => console.log(err.toString()));
        }
    }

    handleValue() {
        const sourceAddresses = this.getValue();
        if(!sourceAddresses.replace(/(^\s*)|(\s*$)/g, "")){
            popup(<Snackbar message="请填写需要匹配的地址"/>);
            return;
        }
        let source = sourceAddresses.split('\n');
        let sour = '';
        for(let i=0;i<source.length;i++){
            if(i == source.length-1){
                sour += source[i];
            }else {
                sour += source[i] + '&&';
            }
        }
        let param = new FormData();
        param.append('sourceAddresses', sour);
        param.append('isEdit', true);
        fetch(Uniplatform.context.url + '/nlap/admin/address/match/match/results', {
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
                let result = [];
                data.result.map(function (item) {
                    result.push({
                        source : item.source,
                        target : item.target[0]
                    });
                });
                this.setState({result});
            }).catch((err) => console.log(err.toString()));
    }




    render() {
        let {result,isExcel} = this.state;
        const contents = '山西省太原市杏花岭区鼓楼街道办事处半坡西街16楼1-15\n山西省太原市迎泽区庙前街道办事处水西关街10号院纺织宿舍3号楼2单元1号\n山西省太原市杏花岭区鼓楼街道办事处半坡西街13号楼1栋2单元4层12号';
        return (
            <Page>
                <COMM_HeadBanner prefix=" "/>
                <div style={{background: 'linear-gradient(to bottom, #C9C9C9 0, #f2f2f2 100%)',width:'100%',height:'5px',zIndex:'1111'}}></div>
                <Row style={{minHeight:'680px'}}>
                    <Col size={{ normal: 3, small: 24, medium: 3, large: 3 }} style={{marginTop:'-5px',padding:'0',backgroundColor:'#fff',height:'880px',zIndex:'21'}}>
                        <div style={{position:'relative',padding:'0',background: 'linear-gradient(to bottom, #C9C9C9 0, #fff 100%)',height:'5px',width:'100%',zIndex:'21'}}> </div>
                        <div>
                            <ProductMenu url={'/nlap/product/addressMatch'}/>
                        </div>
                    </Col>
                    <Col size={{ normal: 21, small: 24, medium: 21, large: 21 }} style={{padding:'0'}}>
                        <Row style={{margin:'10px 20px'}}>
                            <img src="../image/in15.png"/><span className="classTitle">地址匹配</span>
                        </Row>
                        <Row className="enterContent" style={{margin:'0 20px',height:'300px'}}>
                            <Row style={{margin:'30px 30px 10px 30px'}}>
                                <Label>来源</Label>
                                <Select value="DB" showClear={false} getter={ this.formGetterSource.bind(this) }
                                        onChange={this.handleChange.bind(this)}
                                        style={{width:'20%',display:'inline-block',marginRight:'10px'}}>
                                    <Option value="DB">DB</Option>
                                    <Option value="Excel">Excel</Option>
                                </Select>
                                {isExcel ? <Label>路径</Label> : <Label>表名</Label>}
                                <Input placeholder="请输入表名" style={{display:'inline-block',width:'20%'}} getter={ this.formGetterOne.bind(this) }/>
                                {isExcel ? <Label>列数</Label> : <Label>列名</Label>}
                                <Input placeholder="请输入列名" style={{display:'inline-block',width:'20%'}} getter={ this.formGetterTwo.bind(this) }/>

                                <Button type="info" style={{margin:'0 20px'}} onClick={this.improt.bind(this)}>导入</Button>
                            </Row>
                            <Textarea style={{margin:'30px 30px 10px 30px',maxHeight:'150px'}} placeholder="请输入文本. . ." rows={8}
                                      getter={ this.formGetter } value={contents}/>
                            <Button type="info" style={{float:'right',marginRight:'30px'}} onClick={this.handleValue.bind(this)}>匹配</Button>
                        </Row>
                        <br/>
                        <Row className="dicManage_row">
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} className="dicManage_col">
                                <div className = "dicManage_title" >
                                    <span className="dicManage_span">匹配结果</span>
                                </div>
                            </Col>
                            <div className="dicManage_content">
                                <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }} style={{margin:'20px 23px',width:'95%'}}>
                                    {result ?
                                        <Table striped={true} multiLine={true} dataSource={result} bodyScrollable>
                                            <Column dataIndex="source" title="源地址" scaleWidth='50%' textAlign="center"/>
                                            <Column dataIndex="target" title="匹配结果" scaleWidth='50%' textAlign="center"/>
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
AddressMatch.UIPage = page;
export default AddressMatch;