import React, { Component } from 'react';
import {Page,Row,Col,Table,Column,Divider,Button,Icon,Text,Pagination,context,
    Modal,ModalHeader,ModalBody,ModalFooter,Form,FormItem,Label,Input,Snackbar,popup,Checkbox,Dialog,Option,Select,Upload,Tabs,Tab,ButtonGroup,Container,Textarea} from 'epm-ui';
import { SingleBanner,Footer,COMM_HeadBanner,UsouMenu,NavigationBar } from '../../components/uniplatform-ui';

const contextUrl = '/uniplatform';

const page = {
    title: 'Usou',
    css: [
        'css/clusters.min.css',
        'css/singleBanner.min.css',
        'css/leftnav.min.css'
    ]
};

class participleManage extends Component {
    constructor(props){
        super(props);
        this.state = {
            data:[],
            visible: false,
            uploadVisible:false,
            alterVisible:false,
            select:null,
            id:0,
            dic:"dic",
            indexData:null,
            typeData:null,
            fieldData:null,
            typesFields:null,
            tokens:[],
            tokensText:[],
            deleteWord:null,
            index:1,
            total:0,
            size:10,
            searchData:[]
        };

        this.addWord=this.addWord.bind(this);
        this.uploadWord=this.uploadWord.bind(this);
        this.confirmBatchDelete=this.confirmBatchDelete.bind(this);
        this.batchDelete=this.batchDelete.bind(this);
        this.alterWord=this.alterWord.bind(this);
        this.confirmDelete=this.confirmDelete.bind(this);
        this.deleteWord=this.deleteWord.bind(this);
        this.handleSelect=this.handleSelect.bind(this);
        this.handleClick=this.handleClick.bind(this);
        this.handleValue=this.handleValue.bind(this);
        this.formTirgger=this.formTirgger.bind(this);
        this.formGetter=this.formGetter.bind(this);
        this.handleClose=this.handleClose.bind(this);
        this.noSelect=this.noSelect.bind(this);

        this.addHandleSubmit=this.addHandleSubmit.bind(this);
        this.handleResponse=this.handleResponse.bind(this);
        this.formTirggerWord=this.formTirggerWord.bind(this);
        this.loadAnalyzerData=this.loadAnalyzerData.bind(this);
        this.formGetterAnalyzer=this.formGetterAnalyzer.bind(this);
        this.formGetterInput=this.formGetterInput.bind(this);
        this.formGetterIndex=this.formGetterIndex.bind(this);
        this.formGetterType=this.formGetterType.bind(this);
        this.formGetterField=this.formGetterField.bind(this);

        this.indexChange=this.indexChange.bind(this);
        this.typeChange=this.typeChange.bind(this);
        this.formTirggerText=this.formTirggerText.bind(this);
        this.formGetterText=this.formGetterText.bind(this);
        this.handleAnalyze=this.handleAnalyze.bind(this);
        this.handleAnalyzeText=this.handleAnalyzeText.bind(this);

    }

    getWordsList(){

    }

    componentDidMount() {
        this.fetchUsouIndexList(0);
        this.loadAnalyzerData();
    }

    fetchUsouIndexList(page) {

        let data = new FormData();
        data.append("body", `{"key":"${this.state.dic}","page":"${page}"}`);

        fetch( contextUrl + "/usou/cluster/"+this.props.page.id+"/_analyze/dic/get", {
            credentials: 'same-origin',
            method: 'POST',
            body:data
        } )
            .then( ( response ) => response.json())
            .then( ( responseData ) => {
                this.setState({total:responseData.data.count[0].count});
                this.setState({data: responseData.data.data});
            }).catch( ( err ) => console.log( err.toString()));

    }
    exchangePage(index,size){
        if (this.state.searchData.length > 10){
            this.setState({data:this.state.searchData.slice((index-1)*size,index*size)});
        }else {
            this.fetchUsouIndexList(index-1);
        }
        this.setState({index:index})
    }

    handleClose() {
        this.resetWord();
        this.setState( { visible: false } );
    }

    uploadHandleClose(){
        this.setState( { uploadVisible: false } );
    }

    handleValue( ) {
        const data = this.getValue();
        let str = JSON.stringify( data, null, 2 );
        this.setState( {
            data: str
        } )

    }

    formTirgger( trigger ) {
        this.reset = trigger.reset;
    }

    formGetter( getter ) {
        this.getValue = getter.value;
    }

    formGetterAnalyzeText( getter ) {
        this.getValueAnalyzeText = getter.value;
    }

    formTirggerAnalyzeText( trigger ) {
        this.resetAnalyzeTex = trigger.reset;
    }

    addWord(){
        this.setState( { visible: true } );
    }

    noSelect(){
        popup(<Dialog
            title="是否选择"
            message="您没有选择，请重新选择"
            icon="warn"
        />);
    }

    alterWord(){
        this.setState( { alterVisible: true } );
    }
    uploadWord(){
        this.setState( { uploadVisible: true } );
    }

    confirmBatchDelete(){
        if (this.state.select==null||this.state.select==""){
            this.noSelect();
        }else {
            const approveBtn = {
                text: "确认",
                type: "primary",
                onClick: this.batchDelete
            };
            const cancelBtn = { text: "取消" }
            popup(<Dialog
                title="批量删除"
                message="确认批量删除"
                type="confirm"
                showCancelBtn
                cancelBtn={ cancelBtn }
                showApproveBtn
                approveBtn = { approveBtn }
            />);
        }

    }
    batchDelete( after ){
        let data = new FormData();

        data.append("key",this.state.dic);
        data.append("keywords",this.state.select);
        let url=contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/flush/dic/mulDelete";

        fetch(url,{method:'post',body:data,credentials: 'same-origin',})
            .then((response)=>response.json())
            .then((responseData)=>{
                this.handleResponse(responseData,"批量删除");
            })
            .catch((err)=>console.log(err));
        after( true );
    }
    handleSelect( data ) {
        this.setState( { select: data.map( ( key ) => key.word ).join(',')});
    }
    handleClick() {
        let input = this.getValueInput();

        const url = contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/dic/search";
        let data = new FormData();
        data.append("body",`{"search":"${input}","key":"${this.state.dic}"}`);

        fetch( url, {method:'post',body:data, credentials: 'same-origin'} )
            .then( ( response ) => response.json() )
            .then( ( searchData ) => {
                let array=searchData.data.data;
                this.setState({data:array.slice(0,10)});
                this.setState({searchData:array});
                this.setState({total:array.length});
                this.setState({index:1});

            })
            .catch( ( err ) => console.error( url, err.toString() ) );
    }

    handleInput( data ) {
        this.setState( { keyWords: data } );
    }
    addHandleSubmit(data){
        data['key']=this.state.dic;
        let str = JSON.stringify(data,null,2);
        let body= "body";
        data['body'] = str;
        this.setState( { visible: false } );
        this.resetWord();
        return data;
    }

    formGetterAnalyzer(getter){
        this.getValueAnalyzer=getter.value;
    }

    formGetterIndex(getter){
        this.getValueIndex=getter.value;
    }

    formGetterType(getter){
        this.getValueType=getter.value;
    }
    formGetterField(getter){
        this.getValueField=getter.value;
    }
    handleResponse(data,tit){
        let status=data["status"];
        let message=data["message"];
        if(status=="200"){
            popup(<Dialog
                title={tit}
                message="操作成功"
            />);
            this.fetchUsouIndexList(this.state.index-1);
        }else if(status!="200"){
            popup(<Dialog
                title={tit}
                message="操作失败"
                icon="danger"
            />);
        }
    }

    formTirggerWord(trigger){
        this.resetWord=trigger.reset;
    }

    formGetterText(getter){
        this.getValueText=getter.value;
    }

    formTirggerText(trigger){
        this.resetText=trigger.reset;
    }

    formGetterInput(getter){
        this.getValueInput=getter.value;
    }

    handleAnalyze(){

        let index = this.getValueIndex();
        let type = this.getValueType();
        let field = this.getValueField();
        let text = this.getValueAnalyzeText();

        let data = new FormData();
        data.append("text",text);

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/"+index+"/"+field, {method:'post',credentials: 'same-origin',body:data})
            .then((response)=>response.json())
            .then((responseData)=>{this.setState({tokens:responseData.data.tokens});})
            .catch((err)=>console.log(err));
    }

    handleAnalyzeText(){

        let analyzer = this.getValueAnalyzer();
        let text = this.getValueText();


        let data = new FormData();
        data.append("text",text);

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/"+analyzer, {method:'post',credentials: 'same-origin',body:data})
            .then((response)=>response.json())
            .then((responseData)=>{this.setState({tokensText:responseData.data.tokens});})
            .catch((err)=>console.log(err));
    }


    loadAnalyzerData(){

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/_cat/indices", {method:'get',credentials: 'same-origin'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let indices = this.parseIndex(responseData.data);
                let indexData=this.parseSource(indices);
                this.setState({indexData:indexData})})
            .catch((err)=>console.log(err));
    }

    parseIndex(data){
        let indices = new Array();
        for (let index in data){
            if (data[index].status=="open"){
                indices.push(data[index].index);
            }
        }
        return indices;
    }

    indexChange( dataString, isValid){

        fetch(contextUrl+"/usou/cluster/"+this.props.page.id+"/"+dataString+"/_mapping"
            ,{method:'post',credentials: 'same-origin'})
            .then((response)=>response.json())
            .then((responseData)=>{
                let typesFields = this.parseMapping(responseData);
                let typeData=this.parseSource(typesFields.keys());
                this.setState({typesFields:typesFields});
                this.setState({typeData:typeData})})
            .catch((err)=>console.log(err));
    }

    parseMapping(responseData){
        let typesFields = new Map();
        for(let mapping in responseData){
            let mappings = responseData[mapping].mappings;
            for (let types in mappings){
                let properties = mappings[types];
                for (let field in properties){
                    typesFields.set(types,Object.keys(properties[field]));
                }
            }
        }
        return typesFields;
    }

    typeChange(dataString, isValid){
        let typesFields = this.state.typesFields;
        let fields = typesFields.get(dataString);
        let fieldData = this.parseSource(fields);
        this.setState({fieldData:fieldData});
    }

    parseSource(source){
        let array = new Array();
        for(let s of source){
            let temp={};
            temp["text"]=s;
            temp["value"]=s;
            array.push(temp);
        }
        return array;
    }

    confirmDelete(word){
        this.setState({deleteWord:word});
        const approveBtn = {
            text: "确认",
            type: "primary",
            onClick: this.deleteWord
        };
        const cancelBtn = { text: "取消" }
        popup(<Dialog
            title="删除词"
            message="确认删除"
            type="confirm"
            showCancelBtn
            cancelBtn={ cancelBtn }
            showApproveBtn
            approveBtn = { approveBtn }
        />);
    }

    deleteWord(after){

        let data = new FormData();
        let body = new Object();
        body.key=this.state.dic;
        body.keyword=this.state.deleteWord;

        data.append("body",JSON.stringify(body));

        // data.append("key",this.state.dic);
        // data.append("keyword",this.state.deleteWord);

        let url=contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/flush/dic/delete";

        fetch(url,{method:'post',body:data,credentials: 'same-origin',})
            .then((response)=>response.json())
            .then((responseData)=>{
                this.handleResponse(responseData,"删除");
            })
            .catch((err)=>console.log(err));
        after( true );
    }

    handleKeyPress( event ) {
        if (event.which==13){
            this.handleClick();
        }
    }

    render() {
        let {visible,uploadVisible,alterVisible,data,indexData,typeData,fieldData,tokens,tokensText,index,total,size} = this.state;

        return (
            <Page>
                <COMM_HeadBanner prefix="搜索引擎"/>
                {/*<SingleBanner prefix="搜索引擎" id={this.props.page.id}/>*/}
                <Divider/>
                <Row style={{minHeight: '600px'}}>
                    <Col size={{ normal: 4, small: 24, medium: 4, large: 4 }}>
                        <UsouMenu url={'/usou/participleManage_Test'} id={this.props.page.id} name={this.props.page.name}/>
                    </Col>
                    <Col size={{normal:20, small:24, medium:20, large:20}}>
                        <Row>
                            <Col size={{ normal: 24, small: 24, medium: 24, large: 24 }}>
                                <NavigationBar code={'participleManage_Test'+this.props.page.name} innerId={this.props.page.id} name={this.props.page.name}/>
                            </Col>
                        </Row>
                        <Container className="show-grid" type="fluid">
                            <Row>
                                <Col size={11} style={{border:'1px #0066CC solid'}}>
                                    <h2  style={{margin:'40px 10px 20px 10px'} }>根据字段分词</h2>
                                    <Row>
                                    <Col size={ 8 }>
                                        <Text isBold={ true } style={{margin:'10px 25px 10px 0px'}}>索引名称</Text>
                                    </Col>
                                    <Col size={ 8 }>
                                        <Text isBold={ true } style={{margin:'10px 25px 10px 0px'}}>类型名称</Text>
                                    </Col>
                                    <Col size={ 8 }>
                                        <Text isBold={ true } style={{margin:'10px 25px 10px 0px'}}>字段名称</Text>
                                    </Col>
                                </Row>
                                    <Row>
                                        <Col size={ 8 }>
                                            <Select placeholder="索引名称" dataSource={indexData}
                                                    search={ true } multiple={ false }
                                                    onChange={this.indexChange} getter={this.formGetterIndex}
                                                    >
                                            </Select>
                                        </Col>
                                        <Col size={ 8 }>
                                            <Select placeholder="类型" dataSource={typeData}
                                                    search={ true } multiple={ false } name="select"
                                                    onChange={this.typeChange} getter={this.formGetterType}>
                                            </Select>
                                        </Col>
                                        <Col size={ 8 }>
                                            <Select placeholder="字段名称"
                                                    search={ true } multiple={ false }
                                                    style={{margin:'0px 0px 50px 0px'}} dataSource={fieldData}
                                                    getter={this.formGetterField}>
                                            </Select>
                                        </Col>
                                        <Textarea style={{margin:'0px 10px 0px 10px'}} placeholder="请输入" rows={ 4 } name="textarea" trigger={ this.formTirggerAnalyzeText.bind(this) } getter={ this.formGetterAnalyzeText.bind(this) }/>

                                        <Button type="primary" style={{float:'right',margin:'0px 10px 60px 10px'}} onClick={ this.handleAnalyze }>分词</Button>
                                    </Row>

                                    <Row>
                                        {
                                            Object.keys(tokens).map((index) => {
                                                return <Col size={7} key={index} style={{textAlign:'center',border: '1px solid #ccc',marginBottom: '10px',paddingLeft:'0px',paddingRight:'0px',marginRight:'10px'}}><p>{tokens[index].token}</p><p>pos:{tokens[index].position} start:{tokens[index].start_offset} end:{tokens[index].end_offset}</p> </Col>
                                            })
                                        }

                                    </Row>
                                </Col>

                                <Col size={2}></Col>
                                <Col size={11} style={{border:'1px #0066CC solid'}}>
                                    <h2  style={{margin:'40px 10px 20px 10px'} }>根据分词器分词</h2>

                                    <Row>
                                        <Col size={ 8 } >
                                            <Text isBold={ true } style={{margin:'10px 25px 10px 0px'}}>分词器名称</Text>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col size={ 12 } >
                                            <Select placeholder="分词器名称"
                                                    search={ true } multiple={ false }
                                                    style={{margin:'0px 0px 50px 0px'}} getter={this.formGetterAnalyzer} >
                                                <Option value="query_ansj">query_ansj</Option>
                                                <Option value="index_ansj">index_ansj</Option>
                                                <Option value="nlp_ansj">nlp_ansj</Option>
                                                <Option value="dic_ansj">dic_ansj</Option>
                                                <Option value="base_ansj">base_ansj</Option>
                                                <Option value="whitespace">whitespace</Option>
                                                <Option value="standard">standard</Option>
                                                <Option value="simple">simple</Option>
                                            </Select>
                                        </Col>
                                        <Textarea style={{margin:'0px 10px 0px 10px'}} placeholder="请输入" rows={ 4 } name="textarea" trigger={ this.formTirggerText } getter={ this.formGetterText }/>

                                        <Button type="primary" style={{float:'right',margin:'0px 10px 60px 10px'}} onClick={ this.handleAnalyzeText }>分词</Button>
                                    </Row>
                                    <Row>
                                        {
                                            Object.keys(tokensText).map((index) => {
                                                return <Col size={7} key={index} style={{textAlign:'center',border: '1px solid #ccc',marginBottom: '10px',paddingLeft:'0px',paddingRight:'0px',marginRight:'10px'}}><p>{tokensText[index].token}</p><p>pos:{tokensText[index].position} start:{tokensText[index].start_offset} end:{tokensText[index].end_offset}</p> </Col>
                                            })
                                        }

                                    </Row>
                                </Col>
                            </Row>
                        </Container>
                    </Col>
                </Row>

                <Modal visible={ visible } onClose={ this.handleClose.bind( this ) }  >
                    <ModalHeader>
                        添加词
                    </ModalHeader>
                    <ModalBody height={ 220 }>

                        <Form type="horizontal" async={ true }
                              action= {contextUrl+"/usou/cluster/"+this.props.page.id+"/_analyze/flush/dic/insert"}
                              method="post" onSubmit={this.addHandleSubmit}
                              onAfterSubmit={this.handleResponse} trigger={this.formTirggerWord}>
                            <FormItem  name={"keyword"} required={true}>
                                <Label>词名称</Label>
                                <Input name="keyword" required={true}/>
                            </FormItem>

                            <FormItem name={"nature"}>
                                <Label>词属性</Label>
                                <Select  name="nature" placeholder="Please select" multiple={ false }>
                                    <Option value="n" >n</Option>
                                    <Option value="v" >v</Option>
                                    <Option value="nv" >nv</Option>
                                    <Option value="a" >a</Option>
                                    <Option value="adj" >adj</Option>
                                </Select>
                            </FormItem>

                            <FormItem name={"freq"}>
                                <Label>词频</Label>
                                <Input name="freq"/>
                            </FormItem>

                            <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                            <Button type="primary" htmlType="submit" >确定</Button>
                        </Form>
                    </ModalBody>
                </Modal>

                <Modal visible={ alterVisible } onClose={ this.handleClose.bind( this ) }  >
                    <ModalHeader>
                        修改词
                    </ModalHeader>
                    <ModalBody height={ 200 }>

                        <Form type="horizontal" >
                            <FormItem name={"input"}>
                                <Label>词名称</Label>
                                <Input name="word"/>
                            </FormItem>

                            <FormItem name={"input"}>
                                <Label>词性</Label>
                                <Input name="natural"/>
                            </FormItem>

                            <FormItem name={"input"}>
                                <Label>词频</Label>
                                <Input name="freq"/>
                            </FormItem>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
                        <Button type="primary" htmlType="submit">确定</Button>
                    </ModalFooter>
                </Modal>

                <Modal visible={ uploadVisible } onClose={ this.uploadHandleClose.bind( this ) } >
                    <ModalHeader>
                        上传词
                    </ModalHeader>
                    <ModalBody height={ 220 }>
                        <FormItem >
                            <Label>文件路径</Label>
                            <Upload placeholder="选择文件"
                                    action={contextUrl + "/usou/cluster/"+this.props.page.id+"/_analyze/dic/"+this.state.dic+"/upload"}
                                    limit={ 1 }  />
                        </FormItem>
                        <Text color="skyblue">1.系统支持.txt格式文件进行上传</Text>
                        <Text color="skyblue">2.txt文件的<a href="/usou/resource/dic_example_file.txt">示例文件</a></Text>
                        {/*<Text color="skyblue">2.csv文件的示例文件</Text>*/}
                    </ModalBody>
                    <ModalFooter>
                        <Button onClick={ this.uploadHandleClose.bind( this ) }>关闭</Button>
                        <Button onClick={this.uploadHandleClose.bind( this )} type="primary" htmlType="submit">确定</Button>
                    </ModalFooter>
                </Modal>

                <Footer/>
            </Page>
        );
    }
}
participleManage.UIPage = page;
export default participleManage;
