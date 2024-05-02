import java.lang.*;
import java.util.*;

public class Find_E1
{
	ElementaryTable E1 = new ElementaryTable();	          //記錄「 Erasable 1-itemsets 」	  
	
	//---------------------------------
	// 輸入參數設定	
	double[] maxGainThreshold;
	char[][] dbData;
	int[] transProfit;
	double totalGain=0d;
	
	//-------------------------------
	// 取得「處理結果」		
	int CK_Num=0;
	int EI_Num=0;
	int maxLength =0;
	
	//-------------------------------
	//取E1的使用的記憶體
	//static	float freeMemory, totalMemory; 		//宣告未使用前記憶體狀況與使用後記憶體狀況，兩者相減就是使用記憶體量！
	//static  long useMemory;
	static	Runtime r = Runtime.getRuntime(); 	//這是宣告相關參數
	//static	String usedStr;   			        //列印「使用記憶體量」字串
			
	public Find_E1(char[][] dbData, int[] transProfit, double[] maxGainThreshold, double totalGain) //建構子
	{
		try
		{		
			//------------------------------------------
			// 設定「初始值」
			this.maxGainThreshold = maxGainThreshold;//Gain門檻
			this.dbData = dbData; // 產品
			this.transProfit = transProfit; // 收益
			this.totalGain = totalGain; //總收益
							
			//freeMemory = (float) r.freeMemory();//未使用前的記憶體！
			//------------------------------------------
			// 01. get the set of E1
		 	Find_E1();			
			
			//System.out.println(" maxLength:"+maxLength);
						
			//totalMemory = (float) r.totalMemory(); //使用了多少記憶體！ 
			//diffmemory_int = ((int) (totalMemory - freeMemory)/1024);
			//usedStr = String.valueOf(diffmemory_int)+ "K used";   
			//System.out.println(" Mining Erasable 1-Itemsets  在記憶體方面，共使用: "+usedStr); 						
			}
			catch(Exception ex)
		    {
				System.out.println(" Error about finding Erasable 1-Itemsets information (in Find_E1.java):"+ex.getMessage());
		    }		
	  }
	
	
	  //------------------------------------------------------------------	
	  //------------------------------------------------------------------	
	  //回傳「相關結果資訊」
	  public ElementaryTable getE1()
	  {				// get the set of E1
		return E1;
	  }

	  public int getCK_Num()
	  {
		return CK_Num;	
	  }	

	  public int getMaxLength()
	  {
		return maxLength;
	  }

	  //public long getE1_memory()
	  //{
		//return useMemory;
	  //}	
		
    
    // find the set of erasable 1-itemsets from db
    public void Find_E1()
    {
		try
		{
			//------------------------------------------
			ElementaryTable C1 = new ElementaryTable();		//記錄「 Candidate 1-itemsets 」
													
		    //------------------------------------------
			// (1) the gain value of each candidate 1-itemset
			//Record the item and sum its gain value
			for(int i=0;i<dbData.length;i++) //產生candidate 1-itemset並計算1-itemset gain值
			{						
				for(int j=0;j<dbData[i].length;j++)
				{
					C1.add(dbData[i][j], transProfit[i]);
				}	
				// get the maximum length of transactions
				if(maxLength <dbData[i].length)
				{
					maxLength =dbData[i].length; //紀錄product生產最多材料數
				}
			}
			CK_Num = C1.size();    //the number of all items appear in the DB				
				
			//------------------------------------------						
			// 取得「Erasable 1-Itemsets  」							
			Set set1 = C1.ElementSet();
     		Iterator iter1 = set1.iterator();
			Element nx1;
			
			while (iter1.hasNext())    
			{
				nx1 = (Element)iter1.next();
				double eGain = ((double)nx1.getValue());
				/*
				System.out.print(" Itemset: "+ (int)nx1.getKey()[0]);
                System.out.print(" Gain: "+ (int)nx1.getValue());
                System.out.println(" Gain_Threshold: "+ maxGainThreshold[(int)nx1.getKey()[0]]);
				*/
				if(eGain<=maxGainThreshold[(int)nx1.getKey()[0]])
				{
					E1.add(nx1.getKey(), nx1.getValue());
  					//System.out.print(" itemset: "+ (int)nx1.getKey()[0]+", ");
                    //System.out.println(" gain: "+ (int)nx1.getValue());
				}
			}
			//System.out.println(" C1.size() = "+C1.size());
			//System.out.println(" E1.size() = "+E1.size());
			
			//釋放空間!
			//r.gc();
			//useMemory = r.totalMemory() - r.freeMemory();
			C1=null;
			r.gc();
			System.out.println ((r.totalMemory()-r.freeMemory())/1024 + ("K"));
		}
		catch(Exception e)
		{
			System.out.println(" Error about finding the erasable 1-itemsets: "+e.getMessage());	
        }		
    }

	  //------------------------------------------------------------------	
	  //------------------------------------------------------------------			
	  //列印區
	  /*public void Print(ElementaryTable E1)
	  {
		    try{
			      System.out.println("----------------------------------");
			      System.out.println(" Print E1 ");
			      System.out.println();
			      //編號(第幾筆)
			      int tid=1;			
			
			      Set set1 = E1.ElementSet();
			      Iterator iter1 = set1.iterator();
			      Element nx1;
			
			      while(iter1.hasNext())
			      {				
				        nx1 = (Element)iter1.next();				
				        
				        System.out.println("------------------------------------");
				        System.out.print(" itemset = ");
				        for(int i=0;i<nx1.getKey().length;i++)
				        {
					          System.out.print( (int)nx1.getKey()[i] + " gain = "+nx1.getValue());
				        }
				        System.out.println();
				
				        System.out.println();
				        System.out.println("------------------------------------");
				        System.out.println();
				
				        tid++;
			      }			
			      System.out.println();
			      System.out.println("----------------------------------");
		    }catch(Exception ex)
		    {
			      System.out.println(" Error about printing E1  (in Find_E1.java):"+ex.getMessage());
		    }
	  }	*/
}