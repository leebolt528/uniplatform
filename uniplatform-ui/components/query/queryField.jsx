import React, { Component } from 'react';
import {Select,Option,Input,FormItem} from 'epm-ui';
import {QueryInput} from './queryInput';
import {QueryRange} from './queryRange';
import {QueryFuzzy} from './queryFuzzy';

class QueryField extends Component {
  
  constructor( props ) {
    super( props );
    
    this.state= {
      field: props.field,
      config : 'term',
      allfield : {}
    }
    this.fileQuery = this.fileQuery.bind(this);
    this.fileMiss = this.fileMiss.bind(this);

  } 
  
  componentDidMount() {
    this.fileQuery();
  }

  componentWillReceiveProps( nextProps ) {
    if ( this.props.field !== nextProps.field ) {
      this.setState( { field : nextProps.field } ,() => (this.state.config == "missing") ? this.fileMiss() : this.fileQuery());
    }
  }

  fileQuery(data){
    let config = this.state.config;
    let selField = {} , allfield = {} ,datafield = {};
    selField[this.state.field] = data;
    datafield[config] = selField;
    allfield["data"] = datafield;
    this.setState({ allfield });
    this.props.getQueryField(allfield);
  }

  fileMiss(){
     let config = this.state.config;
     let selField = {} , allfield = {} , datafield = {};
     selField["field"] = this.state.field;
     datafield["exists"] = selField;
     allfield["data"] = datafield;
     this.props.getQueryField(allfield);
  }

  changeSelect(data){
      let config = data;
      this.setState({ config }, () => (config == "missing") ? this.fileMiss() : this.fileQuery());
  }

  changeQuery(){
    let config = this.state.config;
     if(config == ''){
          return '';
      }else if(config == 'missing'){
          return '';
      }
      else if(config == 'fuzzy'){
          return (
              <QueryFuzzy getQueryfuzzy={this.fileQuery}/>
          );
      }else if(config == 'range'){
          return (
              <QueryRange getQueryRange={this.fileQuery}/>
          );
      }else{
          return (  <QueryInput getQueryInput={this.fileQuery}/>  );
      }
  }

  render() {
    return (
       <div style={{display: 'inline-block'}}>
        <div style={{display: 'inline-block'}}>
          <Select value={ 'term' } onChange={this.changeSelect.bind(this)}>
            <Option value="term">term</Option>
            <Option value="wildcard">wildcard</Option>
            <Option value="prefix">prefix</Option>
            <Option value="fuzzy">fuzzy</Option>
            <Option value="range">range</Option>
            <Option value="query_string">query_string</Option>
            <Option value="text">text</Option>
            <Option value="missing">missing</Option>
          </Select>
        </div>
        <div style={{display: 'inline-block'}}>
          {
            this.changeQuery()
          }
        </div>
      </div>
    );
  }

}

export default QueryField;
export {QueryField};