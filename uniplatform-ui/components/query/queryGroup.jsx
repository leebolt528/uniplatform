import React, { Component } from 'react';
import {Select,Option,FormItem,Label } from 'epm-ui';
import {QueryAll} from './queryAll';
import {QueryField} from './queryField';

class QueryGroup extends Component {
  
  constructor( props ) {
    super( props );
    
    this.state= {
      selectFelid : 'match_all',
      queryGroup : 'must',
      queryField : {},
      dataSource : props.dataSource
    }
    this.getQuery = this.getQuery.bind(this);
    this.fieldQuery = this.fieldQuery.bind(this);
  } 

  componentDidMount() {
      this.getQuery();
  }

  componentWillReceiveProps( nextProps ) {
    if ( this.props.dataSource !== nextProps.dataSource ) {
      this.setState( { dataSource : nextProps.dataSource }, this.getClear );
    }
  }

  getClear(){
      let selectFelid = 'match_all';
      let queryGroup = 'must';
      let queryField = {};
      this.setState({ selectFelid,queryGroup,queryField });
  }

  getQuery(){
    let datafield = {};
    let match = {};
    datafield["match_all"] = match;
    let queryField = {
        group : 'must',
        fieldId : this.props.fieldId
    };
    queryField["data"] = datafield;
    this.props.getQueryGroup(queryField);
  }

  changeGroup(dataString){
      let queryGroup = dataString;
      this.setState({ queryGroup }, ()=> this.fieldQueryGroup());
  }  

  //查询条件改变字段
  changeField(dataString){
      let selectFelid = dataString;
      this.setState({selectFelid},() => (selectFelid == 'match_all') ? this.fieldQueryMust() : '');
  }
  
  //接收子组件返回值
  fieldQuery(data){
      let queryField = data;
      this.setState({queryField}, ()=> this.fieldQueryGroup()); 
  }

  //返回数组
  fieldQueryGroup(){
      let selectFelid = this.state.selectFelid;
      if(selectFelid == 'match_all'){
          this.fieldQueryMust();
      }else{
        let {queryGroup,queryField} = this.state;
        queryField["fieldId"] = this.props.fieldId;
        queryField["group"] = queryGroup;
        this.props.getQueryGroup(queryField);
      }
  }

  //返回数组
  fieldQueryMust(){
      let queryGroup = this.state.queryGroup;
      let queryField = {},datafield = {};
      let match = {};
      datafield["match_all"] = match;
      queryField["data"] = datafield;
      queryField["fieldId"] = this.props.fieldId;
      queryField["group"] = queryGroup;
      this.props.getQueryGroup(queryField);
  }

  getSelectField(){
      let selectFelid = this.state.selectFelid;
      if(selectFelid == 'match_all'){
          return '';
      }else if(selectFelid == '_all'){
          return (
              <QueryAll queryAll={ selectFelid } getQueryAll={this.fieldQuery}/>
          )
      }else{
          return (
              <QueryField field={ selectFelid } getQueryField={this.fieldQuery}/>
          )
      }
  }

  render() {
    let { dataSource,selectFelid,queryGroup } = this.state;
    return (
       <div style={{display: 'inline-block'}}>
           <FormItem>
            <Label>查询条件</Label>
            <Select value={ queryGroup } onChange={this.changeGroup.bind(this)}>
                <Option value="must"> must </Option>
                <Option value="must_not"> must_not </Option>
                <Option value="should"> should </Option>
            </Select>
            <Select value={ selectFelid } onChange={this.changeField.bind(this)}>
                {
                    dataSource.map((item, index) => {
                        return (<Option key={index} value={item}>{item}</Option>)
                    })
                }
            </Select>
            { this.getSelectField() }
           </FormItem>
        </div>
    );
  }

}

export default QueryGroup;
export {QueryGroup};