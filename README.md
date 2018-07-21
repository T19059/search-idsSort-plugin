* 环境依赖：
    * elasticsearch >= 5.x
    * pom中对不同elasticsearch版本的依赖，最终插件只能应用在对应版本的elasticsearch上

* 编译安装：
    * 编译：mvn clean package -DskipTests
    * 安装方式: 
        * 直接将target/release/idsSortPlugin-${version}.zip文件解压到${elasticsearch-home}/plugins/下面
       
* 测试：
    * 首先测试是否安装成功`bin/elasticsearch-plugin list`

    * 用测试环境商品索引做测试
     GET item_search_dev/_search
     
		{
		  "query": {
		    "function_score": {
		      "query": {
		        "match_all": {}
		      },
		      "script_score": {
		        "script": {
		          "source": "ids_sort",
		          "lang": "expert_scripts",
		          "params": {
		            "ids": [
		              "225644",
		              "225645",
		              "225647",
		              "91753"
		            ]
		          }
		        }
		      }
		    }
		  },
		  "sort": {
		    "_score": {
		      "order": "asc"
		    }
		  }
		}