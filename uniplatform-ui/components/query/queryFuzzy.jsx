import React, { Component } from 'react';
import {Select,Option,Input,FormItem} from 'epm-ui';

class QueryFuzzy extends Component {
  
  constructor( props ) {
    super( props );
    
    this.state= {
      fuzzy: {},
      sel : 'max_expansions',
      beforeInp : '',
      afterInp : ''
    }

    this.getQuery = this.getQuery.bind(this);
  } 

  getQuery(){
    let fuzzy = {};
    let { sel, beforeInp,afterInp } = this.state;
    if(beforeInp != ''){
      fuzzy["value"] = beforeInp;
    }
    if(afterInp != ''){
      fuzzy[sel] = afterInp;
    }
    this.setState({ fuzzy });

    this.props.getQueryfuzzy(fuzzy);
  }

  changeBeforeInp(option,data){
    let beforeInp = data;
    this.setState({ beforeInp },() => this. getQuery());
  }

  changeAfterInp(option,data){
    let afterInp = data;
    this.setState({ afterInp },() => this. getQuery());
  }

  changeSel(data){
      let sel = data;
      this.setState({ sel },() => this. getQuery());
  }

  render() {
    let {beforeInp,afterInp} = this.state;
    return (
      <div style={{display: 'inline-block'}}>
        <div style={{display: 'inline-block'}}>
          <Input type="text" value={beforeInp} onChange={this.changeBeforeInp.bind(this,"beforeinp")}/>
        </div>
        <div style={{display: 'inline-block'}}>
          <Select value={ 'max_expansions' } onChange={this.changeSel.bind(this)}>
              <Option value="max_expansions">max_expansions</Option>
              <Option value="min_similarity">min_similarity</Option>
          </Select>
        </div>
        <div style={{display: 'inline-block'}}>
          <Input type="text" value={afterInp} onChange={this.changeAfterInp.bind(this,"afterinp")}/>
        </div>
      </div>
    );
  }

}

export default QueryFuzzy;
export {QueryFuzzy};