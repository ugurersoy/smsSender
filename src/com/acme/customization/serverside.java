package com.acme.customization;

import antlr.collections.List;

import com.lbs.data.objects.CustomBusinessObject;
import com.lbs.data.query.IServerQueryFactory;
import com.lbs.data.query.QueryBusinessObjects;
import com.lbs.data.query.QueryParams;
import com.lbs.platform.interfaces.IServerContext;
import com.lbs.platform.server.LbsServerContext;
import com.lbs.remoteclient.IClientContext;
import com.lbs.transport.RemoteMethodResponse;
import com.lbs.util.StringUtil;

public class serverside {

	public Object executeUODSelectQueryWithJDBC(IServerContext contextx, String sqlText) 
    {
          QueryBusinessObjects result = new QueryBusinessObjects();
          Object[] params = new Object[3];
          params[0] = new Boolean(false);//result
          params[1] = new String("");//errorDesc         
          params[2] = new QueryBusinessObjects();
          
          if ((sqlText == null) || (sqlText.compareTo("") == 0)) {
                 params[1] = new String("sqlText empty !");//errorDesc
                 return params;
          }
          
     IServerContext context = LbsServerContext.getSessionlessContext("Direct_Select_Context");
     IServerQueryFactory serverQueryFactory = context.getServerQueryFactory();
     QueryBusinessObjects items = new QueryBusinessObjects();
     try
     {
            QueryParams qryPrms = new QueryParams();
            qryPrms.setDomainless(true);
            serverQueryFactory.executeSelectQuery(sqlText, qryPrms, items, -1);
            
            
                    if ((items != null)&&(items.size() > 0))
                    {
                            params[0] = new Boolean(true);
                            params[1] = new String("");//errorDesc
                            params[2] = items;//(QueryBusinessObject) items.elementAt(0);
                        }else{
                               params[0] = new Boolean(false);
                               params[1] = new String("sqlText empty !");//errorDesc
                            params[2] = null;
                        }
     }
     catch (Exception e)
     {
                 params[0] = new Boolean(false);
                 params[1] = e.getLocalizedMessage();
              params[2] = null;             
         e.printStackTrace();
         //return params;
     }
     return params;
    }
	
	
	public Object test(IServerContext serverContext,String sql,String sql2)
	{
		
		IServerQueryFactory serverQueryFactory = serverContext.getServerQueryFactory();
		
		QueryParams queryParams = new QueryParams();
		queryParams.setCustomization(PrjGlobals.getM_ProjectGUID());
		QueryBusinessObjects item = new QueryBusinessObjects();
		Object[] data = new Object[2];
		
		try {
	    
				serverQueryFactory.executeQuery(sql, queryParams);
				serverQueryFactory.executeSelectQuery(sql2, queryParams, item, 1);
                data[0]=item;
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Object executeUpdateQuery(IServerContext serverContext,String sqlText) {
		IServerQueryFactory serverQueryFactory = serverContext
				.getServerQueryFactory();

		QueryParams queryParams = new QueryParams();
		queryParams.setCustomization(PrjGlobals.getM_ProjectGUID());

		

		try {
			serverQueryFactory.executeQuery(sqlText, queryParams);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}		


}
