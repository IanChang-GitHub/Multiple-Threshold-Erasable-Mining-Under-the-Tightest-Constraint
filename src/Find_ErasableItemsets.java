import java.lang.*;
import java.util.*;


public class Find_ErasableItemsets
{
	  double  maxGainThreshold[] ;
	  char[][] dbData;            //紀錄product database的二維陣列
	  int[] transProfit;          //紀錄每筆交易的profit
	  double totalGain=0d;        //紀錄利潤總和
	  ElementaryTable E1 ;	
	  ElementaryTable E2 ;
	  Runtime r = Runtime.getRuntime();
	  long memory = 0,temp_memory;
	  
	  int maxLength =0;
	
	  ElementaryTable[] EI;		// the set of erasable itemsets of each phase
	  int Current_Len = 0;		// the current itemset length
	  int CK_Num =0;
	  int EI_Num =0;
	 // long tempMemory = 0, useMemory = 0;
	
	public Find_ErasableItemsets(char[][] dbData, int[] transProfit, double maxGainThreshold[],  double totalGain, ElementaryTable E1, ElementaryTable E2, int maxLength)
    {
	      try{			
				    //---------------------------
				    //宣告變數區
				    this.dbData=dbData;
				    this.transProfit = transProfit;
				    this.maxGainThreshold = maxGainThreshold;
				    this.totalGain = totalGain;
				    this.E1 = E1;
				    this.E2 = E2;
				    this.maxLength  = maxLength ;    //itemset最長長度: item個數
			
				    //Print_Parameters();
			
				    initializeEiTables();
			
				    EI[0]=E1;
				    EI[1]=E2;			
				
				    //System.out.println(" EI[0].size() = " + EI[0].size() );
				    //System.out.println(" EI[1].size() = " + EI[1].size() );
				
				    Current_Len = 3; //設定長度為 3
									
 				    if(EI[0].size()==0 || EI[1].size()==0)return;
				
				    EI[0].size();
				    EI[1].size();
					
				    //---------------------
				    //執行探勘程序
				    //System.out.println("*********************************");				
				    //System.out.println("*********************************");				
				    //System.out.println("*********************************");				
				    //System.out.println(" Find the EIs of the "+Current_Len+"-th phase");								
				
				    //---------------------
				    //執行探勘程序
				    while(Current_Len<=maxLength) //長度未達生產產品最多的原料
				    {					
					      System.out.println("-----performing candidate length :"+Current_Len+"-----");
					
					      Sort_C Sort_CI = new Sort_C(EI[Current_Len-2]);//先進行排序動作！(k-1的erasable itemset)

					      // generate the new candidates k-itemsets
					      Gen_C Gen_AC = new Gen_C(Sort_CI.get_Sort_CItemsets(), EI[Current_Len-2], Current_Len);					
					
					      // save the new candidates k+1-itemsets in the EI tables
					      EI[Current_Len-1] = Gen_AC.get_CItemsets();
					      CK_Num+=EI[Current_Len-1].size();
					
					      //System.out.println(" Total number of CK: "+EI[Current_Len-1].size() );
					
					      // calculate the gain of each candidate (k+1)-itemset by their bitmap information
					      
					      findGainInfo_and_findEI();
										
					      EI_Num +=EI[Current_Len-1].size(); //erasable itemset個數
					     /*
					      if(tempMemory > useMemory) {
					    	 useMemory = tempMemory;
					     }
					     */
					     // if the number of erasable itemsets in the current k-th phase is the value of 0, then stop the finding EI procedure.
					     if(EI[Current_Len-1].size()==0)
					     {
						       break;
					     }
					
					     // if the number of erasable itemsets in the current k-th phase is less than the length of the current k-itemset, then stop the finding EI procedure.
					     // downward closure
					     if(EI[Current_Len-1].size()<Current_Len)
					     {
						       break;
					     }				
					     Current_Len++;					
				    }
				    System.out.println ("memory use: " + memory/1024);
		    }
	      catch(Exception e)
		    {
			      System.out.println(" Error about finding out the high average-utility itemsets (in Find_HAUI_Itemsets.java): "+e.getMessage());
		    }
    }

    //--------------------------------------------------
	  //--------------------------------------------------		
	  // 回傳 處理結果 
	
	  public ElementaryTable[] getEI(){	// 回傳「最後的 HAUI 資訊」(HMUPUI已移除未滿足門檻值的itemsets)
		    return EI;
	  }

	  public int getEINum(){
		    return EI_Num;
	  }

	  public int getCKNum(){
		    return CK_Num;
	  }
	  
	  /*
	  public long getEkMemory(){
		    return useMemory;
	  }
	  */
	  //--------------------------------------------------
	  //--------------------------------------------------
	  //--------------------------------------------------
	  //--------------------------------------------------

	  public void findGainInfo_and_findEI()
	  {		
		    try{							   
				    //(1)檢查EI[Current_Len-1]裡的itemsets是否存在產品資料庫dbData的每一筆產品，是的話取出gain值
				    //Example:EI[Current_Len-1] = {A,B}, {C,D},.....
				    //{A,B} => 檢查每筆產品是否涵蓋A or B，有的話，該比產品利潤值為該itemset{A,B}的gain值
				    //(2)延續(1)的做法，如下:
				    //1.取出itemset有含哪些1-item，並放置Kept_Items中
				    //2.在產品資料庫，檢查每筆產品是否有存在Kept_Items，有的話，該筆產品的利潤值為該itemset的gain值
				    //3.檢查每ㄧitemsets的gain值是否滿足threshold，是的話，為erasable itemset
				    
				    ElementaryTable Kept_Items = new ElementaryTable();	// 記錄 candidate itemsets存在哪些 erasable 1-item				    				    
				    
				    //System.out.println(" 刪除前 - "+  EI[Current_Len-1].size()+" 個 candidate itemsets");
												
       			Set set = EI[Current_Len-1].ElementSet(); 
       			Iterator iter = set.iterator();
       			for (int j=0; iter.hasNext(); j++) 
       			{       					
       			    Kept_Items.clear();
       			    Element nx=(Element)iter.next();
       			    double gainMinConstraint = totalGain;
       			    
       			    //1.取出itemset有含哪些1-item，並放置Kept_Items中
       			    for (int k=0; k < nx.getKey().length; k++)
       			    {
       			        char[] checked_itemset = new char[1];
       			        checked_itemset[0] = nx.getKey()[k];
       			        Kept_Items.add(checked_itemset, 0);
       			        if(maxGainThreshold[nx.getKey()[k]] < gainMinConstraint) //找物品最小值門檻
    			        	gainMinConstraint = maxGainThreshold[nx.getKey()[k]];
       			        //System.out.println("Kept_Items.size()" + Kept_Items.size());
       			    }
       			    
       			    //2.在產品資料庫，檢查每筆產品內的原料是否有存在Kept_Items，有的話，該筆產品的利潤值為該itemset的gain值
					      for(int m=0;m<dbData.length;m++)
					      {						
						        for(int n=0;n<dbData[m].length;n++)
						        {																
							          // 確認是否存在「這個 item」，只要有一個既可，因此該產品利潤值既為itemset的gain值
									if(Kept_Items.ContainsKey(dbData[m][n]))
									{											                            
									EI[Current_Len-1].add(nx.getKey(), transProfit[m]);                            
									n = dbData[m].length; //主要使用精神是為了跳出for迴圈
							        }							          
						        }							    
					      }
					      
					      //3.檢查每ㄧitemsets的gain值是否滿足threshold，是的話，為erasable itemset
					      double eGain = ((double)nx.getValue());
					      /*
					      System.out.print(" itemset: ");
					      for(int l=0;l<nx.getKey().length-1;l++)
								System.out.print((int)nx.getKey()[l]+", ");
						  System.out.print((int)nx.getKey()[nx.getKey().length-1]);
			              System.out.println(" gain: "+ nx.getValue());
  						  */
  						  if(eGain<=gainMinConstraint)
  						  {
  						  }
  						  else
  						  {
  							    EI[Current_Len-1].remove(nx.getKey());
					      }
  						  
       			}
       			r.gc();
       			System.out.println ((r.totalMemory()-r.freeMemory())/1024 + ("K"));
       			temp_memory = r.totalMemory()-r.freeMemory();
       			if(temp_memory > memory)
       				memory = temp_memory;
       			//System.out.println(" 刪除後 - "+  EI[Current_Len-1].size()+" 個 candidate itemsets");				
		    }
		    catch(Exception e)	
		    {
			      System.out.println(" Error about finding the gain value of each candidate (in Find_ErasableItemsets.java): "+e.getMessage());
		    }
	  }

	  public void initializeEiTables()
	  {		
		    long t1 = System.currentTimeMillis();	
			  EI = new ElementaryTable[maxLength];	//記錄「每階段的 high transaction itemsets」
			long t2 = System.currentTimeMillis();
    				
		    //System.out.println(" time of initialling hashtables :"+Double.toString((t2-t1)/1000.0)+"s");
	  }
	
	  /*public void Print_Parameters()
	  {		
		  try{
			  
			  System.out.println("---------------------------------");
			  System.out.println(" dbData.length: "+dbData.length);
			  System.out.println(" transProfit.length: "+transProfit.length);
			  System.out.println(" minGain: "+minGain);
				    System.out.println(" totalGain: "+ totalGain);
				    System.out.println(" E1.size(): "+E1.size());
				    System.out.println(" E2.size(): "+E2.size());				
				    System.out.println(" maxLength: "+maxLength);
				    System.out.println("---------------------------------");
		    }catch(Exception e)	
		    {
			      System.out.println(" Error about printing the parameters (in Find_ErasableItemsets.java): "+e.getMessage());
		    }
	  }*/
}