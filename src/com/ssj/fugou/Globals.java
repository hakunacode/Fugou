package com.ssj.fugou;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.app.Activity;
import android.content.Context;

import com.dlten.lib.STD;
import com.dlten.lib.file.CConFile;
import com.dlten.lib.graphics.CPoint;
import com.ssj.fugou.game.GameDoc;
import com.ssj.fugou.sound.MySoundManager;

public class Globals {
	// constants.
    public static boolean m_bSound = true;
	
	// variables.
	public static final int MODE_SURVIVAL = 0;
	public static final int MODE_TIME_ATTACK = 1;
	public static final int MODE_DEMO = 2;
	
	
	public static boolean	TEST_TOOL = false;
//	public static boolean	TEST_TOOL = true;
	public static boolean	DEBUG_MODE = false;
	public static boolean	DEBUG_AUTO_GAME = false;
	public static boolean	DEBUG_AUTO_SCREEN = false;
	public static boolean	DEBUG_LOG = false;
	public static boolean	DEBUG_MANUAL_GAME = false;
	public static boolean	DEBUG_TUMI = false;
	public static boolean	DEBUG_NANDTUMI = false;
	public static boolean	DEBUG_SHOW_CARDS = false;
	public static boolean	DEBUG_CHECKKIFU = false;
	
	public static boolean	m_bVictoryAnim = false;
	public static boolean	m_bAutoGame = false;
	public static boolean	m_bSuspend = false;

//	public static final int	m_nDebugMemtestSize = 1024*1024*1;
//	public static int	m_nDebugMemtest[] = new int[m_nDebugMemtestSize];

	public static Globals			g_Global     = null;
	public static GameDoc			g_GameDoc    = null;
	public static GameDoc			g_docSuspend = null;
	public static MySoundManager	g_Sound      = null;

	public static void createGlobalValues( Activity context ) {
//		DEBUG_MODE = true;
		if (DEBUG_MODE == true) {
			DEBUG_LOG = true;
//			// DEBUG_AUTO_GAME = true;
//			// DEBUG_MANUAL_GAME = true;
//			DEBUG_SHOW_CARDS = true;
//			DEBUG_TUMI = true;
//			DEBUG_NANDTUMI = true;
//			// DEBUG_CHECKKIFU = true;
//			
//			if ( DEBUG_MANUAL_GAME == true ) {
//				DEBUG_SHOW_CARDS = true;
//			}
		}
	
		g_Global     = getInstance();
		g_GameDoc    = new GameDoc();
		g_docSuspend = new GameDoc();

		g_Sound = new MySoundManager();
		g_Sound.init( (Context)context );
//		g_Sound.setEnable(false);	// DEBUG
		
		m_bSuspend = resumeGame();
	}
	
	public static void deleteGlobalValues() {
		g_Sound.destroy();
		g_Sound = null;
		
		g_Global     = null;
		g_GameDoc    = null;
		g_docSuspend = null;
	}
	
	public static class CHARATER_INFO {
		public int nStayCountry;	//初期治める国
		public int nAttack;			//攻撃
		public int nWisdom;			//知恵
		public int nThinking;		//思考No
		public int nLevel;			//レベルNo
		
		public CHARATER_INFO() {
			Init();
		}
		public CHARATER_INFO( int n0, int n1, int n2, int n3, int n4) {
			nStayCountry = n0;
			nAttack = n1;
			nWisdom = n2;
			nThinking = n3;
			nLevel = n4;
		}
		public void Init() {
			nStayCountry = 0;
			nAttack = 0;
			nWisdom = 0;
			nThinking = 0;
			nLevel = 0;
		}
	};

	public static class COUNTRY_INFO {
		public int nSoldiers;	//兵力
		public int nCharacter;	//初期治める人物
		
		public COUNTRY_INFO() {
			Init();
		}
		public COUNTRY_INFO(int n0, int n1) {
			nSoldiers = n0;
			nCharacter = n1;
		}
		public void Init() {
			nSoldiers = 0;
			nCharacter = 0;
		}
	};

	public static class RULE_INFO {
		boolean bKakumei;		//革命
		boolean bMiyakoOti;		//都落ち
		boolean bSpade3;		//スペ３
		boolean bSibari;		//しばり
		boolean b8Kiri;			//8切り
		boolean bJokerKinsi;	//ジョーカー禁止
		boolean b2AgariKinsi;	//2上がり禁止
		
		public RULE_INFO() {
			Init();
		}
		public RULE_INFO(boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5, boolean b6) {
			bKakumei     = b0;
			bMiyakoOti   = b1;
			bSpade3      = b2;
			bSibari      = b3;
			b8Kiri       = b4;
			bJokerKinsi  = b5;
			b2AgariKinsi = b6;
		}
		public void Init() {
			bKakumei     = false;
			bMiyakoOti   = false;
			bSpade3      = false;
			bSibari      = false;
			b8Kiri       = false;
			bJokerKinsi  = false;
			b2AgariKinsi = false;
		}
		public void Copy(RULE_INFO info) {
			bKakumei     = info.bKakumei;
			bMiyakoOti   = info.bMiyakoOti;
			bSpade3      = info.bSpade3;
			bSibari      = info.bSibari;
			b8Kiri       = info.b8Kiri;
			bJokerKinsi  = info.bJokerKinsi;
			b2AgariKinsi = info.b2AgariKinsi;
		}
		public boolean getVals(int id) {
			boolean	bRet = false;
			switch (id) {
			case 0:		bRet = bKakumei;		break;
			case 1:		bRet = bMiyakoOti;		break;
			case 2:		bRet = bSpade3;			break;
			case 3:		bRet = bSibari;			break;
			case 4:		bRet = b8Kiri;			break;
			case 5:		bRet = bJokerKinsi;		break;
			case 6:		bRet = b2AgariKinsi;	break;
			}
			return bRet;
		}
		public void setVals(int id, boolean val) {
			switch (id) {
			case 0:		bKakumei     = val;		break;
			case 1:		bMiyakoOti   = val;		break;
			case 2:		bSpade3      = val;		break;
			case 3:		bSibari      = val;		break;
			case 4:		b8Kiri       = val;		break;
			case 5:		bJokerKinsi  = val;		break;
			case 6:		b2AgariKinsi = val;		break;
			}
		}
	};

	public static class RESULT_INFO {
		int nGame;			//対戦回数
		int nKunsyu;		//君主回数 、、、、兵卒
		int nGunsi;			//軍師回数
		int nBusyou;		//武将回数
		int nTaiTyou;		//隊長回数
		int nHeisou;		//兵卒
		int nKakumei;		//革命
		int nSibari;		//しばり
		int nSpade3;		//スペ３
		int n2Agari;		//2上がり禁止
		int nMiyako;		//都落ち
		
		public RESULT_INFO() {
			Init();
		}
		public void Init() {
			nGame    = 0;
			nKunsyu  = 0;
			nGunsi   = 0;
			nBusyou  = 0;
			nTaiTyou = 0;
			nHeisou  = 0;
			nKakumei = 0;
			nSibari  = 0;
			nSpade3  = 0;
			n2Agari  = 0;
			nMiyako  = 0;
		}
		public void Copy(RESULT_INFO info) {
			nGame    = info.nGame;
			nKunsyu  = info.nKunsyu;
			nGunsi   = info.nGunsi;
			nBusyou  = info.nBusyou;
			nTaiTyou = info.nTaiTyou;
			nHeisou  = info.nHeisou;
			nKakumei = info.nKakumei;
			nSibari  = info.nSibari;
			nSpade3  = info.nSpade3;
			n2Agari  = info.n2Agari;
			nMiyako  = info.nMiyako;
		}
		
		public int getVals(int id) {
			int	nRet = 0;
			switch (id) {
			case 0:		nRet = nGame;		break;
			case 1:		nRet = nKunsyu;		break;
			case 2:		nRet = nGunsi;		break;
			case 3:		nRet = nBusyou;		break;
			case 4:		nRet = nTaiTyou;	break;
			case 5:		nRet = nHeisou;		break;
			case 6:		nRet = nKakumei;	break;
			case 7:		nRet = nSibari;		break;
			case 8:		nRet = nSpade3;		break;
			case 9:		nRet = n2Agari;		break;
			case 10:	nRet = nMiyako;		break;
			}
			return nRet;
		}
		public void setVals(int id, int val) {
			switch (id) {
			case 0:		nGame    = val;		break;
			case 1:		nKunsyu  = val;		break;
			case 2:		nGunsi   = val;		break;
			case 3:		nBusyou  = val;		break;
			case 4:		nTaiTyou = val;		break;
			case 5:		nHeisou  = val;		break;
			case 6:		nKakumei = val;		break;
			case 7:		nSibari  = val;		break;
			case 8:		nSpade3  = val;		break;
			case 9:		n2Agari  = val;		break;
			case 10:	nMiyako  = val;		break;
			}
		}
	};

	public static class SaveData {
		public int 	nUserCountry;
		public int 	nUserSoldiers;
		public int 	nNPCCountry;
		public boolean	bCountryState[] = new boolean [COUNTRY_MAX];
		public boolean	bBGM;
		public boolean	bSE;
		
		public SaveData() {
			Init();
		}
		public void Init() {
			nUserCountry = 0;
			nUserSoldiers = 0;
			nNPCCountry = 0;
			STD.MEMSET(bCountryState, false);
			bBGM = false;
			bSE  = false;
		}
	};
	
	public static final String RESULT_FILE		= "Result.dat";
	public static final String KIFU_FILE		= "Kifu.dat";
	public static final String CONTINUE_FILE	= "Continue.dat";
	public static final String SETINFO_FILE		= "SetInfo.dat";
	public static final String FREENPCS_FILE	= "FreeNPCs.dat";
	public static final String SUSPEND_FILE		= "Suspend.kif";
	private	static final String	m_strSuspendFile = "suspend.plist";

	public static final int MAX_LIMIT		= 999999;
	public static final int MAX_SOLDIERS	= 999999;
	public static final int WIN_SOLDIERS	= 500;
	public static final int LOSE_SOLDIERS	= -1000;
	public static final int ON				= 1;
	public static final int OFF				= 0;
	public static final int NPC_COUNT		= 4;
	
	public static final int
		GM_UNIFY = 0,
		GM_FREE	 = 1;

	public static final int
		SM_USER = 0,
		SM_NPC  = 1;

    public static final int	//階級の名称
		__DAIFUGOU  = 0,	//君主 - 大富豪
		__FUGOU     = 1,	//軍師 - 富豪
		__HEIMIN    = 2,	//武将 - 平民
		__HINMIN    = 3,	//隊長 - 貧民
		__DAIHINMIN = 4,	//兵卒 - 大貧民
		__KUNSYU  = __DAIFUGOU,
		__GUNSI   = __FUGOU,
		__BUSYOU  = __HEIMIN,
		__TAITYOU = __HINMIN,
		__HEISOU  = __DAIHINMIN;

    public static final int
	    CHR_NONE       = -1,
		CHR_CAOCAO     = 0,		//曹操孟徳
		CHR_LIUBI      = 1,		//劉備玄徳
		CHR_SUNQUAN    = 2,		//孫権仲謀
		CHR_YUANSHAO   = 3,		//袁紹本初
		CHR_CHOUN      = 4,		//趙雲子龍
		CHR_GUANWU     = 5,		//関羽雲長
		CHR_ZHANGBI    = 6,		//張飛益徳
		CHR_LYOPO      = 7,		//呂布奉先
		CHR_HAHUDON    = 8,		//夏侯惇元譲
		CHR_DONGTAK    = 9,		//董卓仲穎		-10
		CHR_JIEKALYANG = 10,	//諸葛亮孔明
		CHR_JUYOU      = 11,	//周瑜公瑾
		CHR_SHANZEI    = 12,	//山賊
		CHR_BINGSHI    = 13,	//兵士
		CHR_JINWEIBING = 14,	//近衛兵
		CHR_CHOSUN     = 15,	//貂蝉
		CHR_SOKYOU     = 16,	//小喬
		CHR_MAX        = 17;

    public static final int		// 各国データ : 国数は　26カ国
	    COUNTRY_NONE = -1,
		_PINGYUAN   =  0,	//平原
		_BEIPING    =  1,	//北平
		_BOHAI      =  2,	//勃海
		_ZHENLIU    =  3,	//陳留
		_RUNAN      =  4,	//汝南
		_XIAPI      =  5,	//下?
		_OH         =  6,	//呉
		_HUIJI      =  7,	//会稽
		_XUCHANG    =  8,	//許昌
		_LUOYANG    =  9,	//洛陽 - 10
		_PINGYANG   = 10,	//平陽
		_XIANGYANG  = 11,	//襄陽
		_WULING     = 12,	//武陵
		_CHANGSHA   = 13,	//長沙
		_JIANGXIA   = 14,	//江夏
		_LINGLING   = 15,	//零陵
		_GUIYANG    = 16,	//桂陽
		_NANHAI     = 17,	//南海
		_CHANGAN    = 18,	//長安
		_HANZHONG   = 19,	//漢中 - 20
		_CHANGDU    = 20,	//成都
		_JIANNING   = 21,	//建寧
		_YUNNAN     = 22,	//雲南
		_WUDU       = 23,	//武都
		_JINCHENG   = 24,	//金城
		_XIHAI      = 25,	//西海
		COUNTRY_MAX = 26;

    public static final int
		RULE_KAKUMEI = 0,
		RULE_MIYAKO  = 1,
		RULE_SPADE3  = 2,
		RULE_SIBARI  = 3,
		RULE_8KIRI   = 4,
		RULE_JOKER   = 5,
		RULE_2AGARI  = 6,
		RULE_COUNT   = 7;

    public static final int
		RESULT_GAME    = 0,
		RESULT_KUNSYU  = 1,
		RESULT_GUNSI   = 2,
		RESULT_BUSYOU  = 3,
		RESULT_TAITYOU = 4,
		RESULT_HEISOU  = 5,
		RESULT_KAKUMEI = 6,
		RESULT_SIBARI  = 7,
		RESULT_SPADE3  = 8,
		RESULT_AGARI   = 9,
		RESULT_MIYAKO  = 10,
		RESULT_COUNT   = 11;

    public static final int
		DIRECT_LEFT  = 0,
		DIRECT_RIGHT = 1,
		DIRECT_UP    = 2,
		DIRECT_DOWN  = 3,
		DIRECT_COUNT = 4;

    public static final int
    	FOCUS_TYPE1 = 1,
    	FOCUS_TYPE2 = 2;

    public static final int g_nSoldierConfig[][]  = new int [][]
	{
		{ CHR_CAOCAO,		CHR_JINWEIBING,	CHR_JINWEIBING,	CHR_BINGSHI },		//曹操孟徳
		{ CHR_LIUBI,		CHR_GUANWU,		CHR_ZHANGBI,	CHR_BINGSHI },		//劉備玄徳
		{ CHR_SUNQUAN,		CHR_JUYOU,		CHR_BINGSHI,	CHR_BINGSHI },		//孫権仲謀
		{ CHR_YUANSHAO,		CHR_JINWEIBING,	CHR_BINGSHI,	CHR_BINGSHI },		//袁紹本初
		{ CHR_CHOUN,		CHR_JIEKALYANG,	CHR_BINGSHI,	CHR_SHANZEI },		//趙雲子龍
		{ CHR_GUANWU,		CHR_BINGSHI,	CHR_BINGSHI,	CHR_BINGSHI	},		//関羽雲長
		{ CHR_ZHANGBI,		CHR_BINGSHI,	CHR_SHANZEI,	CHR_SHANZEI },		//張飛益徳
		{ CHR_LYOPO,		CHR_SHANZEI,	CHR_SHANZEI,	CHR_SHANZEI },		//呂布奉先
		{ CHR_HAHUDON,		CHR_CAOCAO,		CHR_BINGSHI,	CHR_BINGSHI },		//夏侯惇元譲
		{ CHR_DONGTAK,		CHR_LYOPO,		CHR_JINWEIBING,	CHR_BINGSHI },		//董卓仲穎		-10
		{ CHR_JIEKALYANG,	CHR_LIUBI,		CHR_BINGSHI,	CHR_BINGSHI },		//諸葛亮孔明
		{ CHR_JUYOU,		CHR_BINGSHI,	CHR_BINGSHI,	CHR_BINGSHI },		//周瑜公瑾
		{ CHR_SHANZEI,		CHR_SHANZEI,	CHR_SHANZEI,	CHR_SHANZEI },		//山賊
		{ CHR_BINGSHI,		CHR_BINGSHI,	CHR_BINGSHI,	CHR_BINGSHI },		//兵士
		{ CHR_JINWEIBING,	CHR_JINWEIBING,	CHR_BINGSHI,	CHR_BINGSHI },		//近衛兵
		{ CHR_CHOSUN,		CHR_DONGTAK,	CHR_LYOPO,		CHR_JINWEIBING },	//貂蝉
		{ CHR_SOKYOU,		CHR_JUYOU,		CHR_SUNQUAN,	CHR_JINWEIBING },	//小喬	
	};

	public static final int g_nCalcSoldierRate[] = new int [] { 150, 125, 100, 75, 50};

	private static int			m_nGameMode;
	private static int			m_nUserCountry;
	private static int			m_nUserCharacter;
	private static int			m_nNPCCountry;
	private static int			m_nNPCCharacter;
	private static int			m_nUserSoldiers;
	private static boolean		m_bCountryState[] = new boolean [COUNTRY_MAX];
	private static boolean		m_bFreeNPCs[]     = new boolean [COUNTRY_MAX];
	private static int			m_nNCPChrs[]      = new int [4];
	private static boolean		m_bBGM = true;
	private static boolean		m_bSE = true;
	private static RULE_INFO	m_stGameRule = new RULE_INFO();
	private static RESULT_INFO	m_stGameResult = new RESULT_INFO();
	private static SaveData		m_stContinueData = new SaveData();
	private static boolean		m_bContinueGame;
	
    private static Globals _instance = null;
    public static Globals getInstance() {
        if( _instance == null ) {
        	_instance = new Globals();
        }
        return _instance;
    }
    
	public Globals() {
		m_bBGM = true;
		m_bSE = true;
		InitFreeNPCs();
		InitGameResult();
		Init();
    	Load();		// added by hrh 2011-0715-1507
	}
	
	public void 	Init() {
		m_nGameMode = GM_UNIFY;
		InitCountryState();
		InitPlayerCountry();
		//InitGameResult();
		InitGameRule();
		m_stContinueData.Init();
		m_bContinueGame = false;	
	}
	public boolean Load() {		// default is global option information
		LoadGameResult();
		LoadSetInfo();
		LoadFreeNPCs();
		return true;
	}
	
	private static void saveSuspendData() {
		//CGameDoc doc;
		//doc.GetGameKifuFromEngine(CGameLogic::getInstance(), TRUE);
		g_docSuspend.SaveSuspend();
	}

	private static void loadSuspendData() {
		g_docSuspend.LoadSuspend();
	}
	
	private static boolean resumeGame() {
		boolean canResume = false;
	    if (CConFile.isFileExist(m_strSuspendFile) == true) {
	    	byte[]	buf = CConFile.read(m_strSuspendFile);
			if (buf != null && buf[0] == 1) {
				canResume = true;
				loadSuspendData();
			}
			CConFile.delete(m_strSuspendFile);
	    }

		return canResume;
	}

	public static void saveSuspendGame() {
	    if (CConFile.isFileExist(m_strSuspendFile) == true) {
			CConFile.delete(m_strSuspendFile);
	    }
	    
    	byte[]	buf = new byte[1];
    	buf[0] = 1;
    	CConFile.write(m_strSuspendFile, buf);
		
		saveSuspendData();
	}
	
	public boolean	Save(boolean bCheck) {
		return true;
	}
	public boolean	SaveGameResult(boolean bCheck) {
		boolean		bRet = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            for (int i = 0; i < 10; i++) {
                dos.writeInt(m_stGameResult.getVals(i));
            }

            byte[] buf = baos.toByteArray();
            dos.close();
            baos.close();
            
            bRet = CConFile.write(RESULT_FILE, buf);
        } catch (Exception e) {
        	STD.printStackTrace(e);
        	bRet = false;
        }
        
        return bRet;
	}
	public boolean	LoadGameResult() {
		boolean		bRet = false;
        byte[] buf = CConFile.read(RESULT_FILE);
        if (buf == null)
            return false;
        
        try {
            ByteArrayInputStream baos = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(baos);
            
            for (int i = 0; i < 10; i++) {
                m_stGameResult.setVals(i, dis.readInt() );
            }

            dis.close();
            baos.close();
            
            bRet = true;
        }
        catch (Exception e) {
        	STD.printStackTrace(e);
            bRet = false;
        }
        
		return bRet;
	}
	public boolean	SaveContinueData(boolean bCheck) {
		SetContinueDataFromGlobal(m_stContinueData);

		boolean		bRet = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(m_stContinueData.nUserCountry);
            dos.writeInt(m_stContinueData.nUserSoldiers);
            dos.writeInt(m_stContinueData.nNPCCountry);
        	for (int i = 0; i < COUNTRY_MAX; i ++) {
        		dos.writeBoolean(m_stContinueData.bCountryState[i]);
        	}
        	dos.writeBoolean(m_stContinueData.bBGM);
        	dos.writeBoolean(m_stContinueData.bSE);

            byte[] buf = baos.toByteArray();
            dos.close();
            baos.close();
            
            bRet = CConFile.write(CONTINUE_FILE, buf);
        } catch (Exception e) {
        	STD.printStackTrace(e);
        	bRet = false;
        }
        
        return bRet;
	}
	public boolean	LoadContinueData() {
		boolean		bRet = false;
        byte[] buf = CConFile.read(CONTINUE_FILE);
        if (buf == null)
            return false;
        
        try {
            ByteArrayInputStream baos = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(baos);

        	m_stContinueData.nUserCountry  = dis.readInt();
        	m_stContinueData.nUserSoldiers = dis.readInt();
        	m_stContinueData.nNPCCountry   = dis.readInt();
        	for (int i = 0; i < COUNTRY_MAX; i ++) {
            	m_stContinueData.bCountryState[i] = dis.readBoolean();
        	}
        	m_stContinueData.bBGM = dis.readBoolean();
        	m_stContinueData.bSE = dis.readBoolean();

            dis.close();
            baos.close();
            
            bRet = true;
        }
        catch (Exception e) {
        	STD.printStackTrace(e);
            bRet = false;
        }
        
		return bRet;
	}
	
	// BOOL	SaveSetInfo(BOOL bCheck = TRUE);
	public boolean	SaveSetInfo(boolean bCheck) {
		boolean		bRet = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

        	dos.writeBoolean(m_bBGM);
        	dos.writeBoolean(m_bSE);

            byte[] buf = baos.toByteArray();
            dos.close();
            baos.close();
            
            bRet = CConFile.write(SETINFO_FILE, buf);
        } catch (Exception e) {
        	STD.printStackTrace(e);
        	bRet = false;
        }
        
        return bRet;
	}
	public boolean	LoadSetInfo() {
		boolean bSetInfo[] = new boolean[2];
		STD.MEMSET(bSetInfo, true);
		
		boolean		bRet = false;
        byte[] buf = CConFile.read(SETINFO_FILE);
        if (buf == null)
            return false;
        
        try {
            ByteArrayInputStream baos = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(baos);

			m_bBGM = dis.readBoolean();
			m_bSE = dis.readBoolean();

            dis.close();
            baos.close();
            
            bRet = true;
        }
        catch (Exception e) {
        	STD.printStackTrace(e);
            bRet = false;
        }
        
		return bRet;
	}
	public boolean	SaveFreeNPCs(boolean bCheck) {
		boolean		bRet = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            for (int i = 0; i < COUNTRY_MAX; i++) {
            	dos.writeBoolean(m_bFreeNPCs[i]);
            }

            byte[] buf = baos.toByteArray();
            dos.close();
            baos.close();
            
            bRet = CConFile.write(FREENPCS_FILE, buf);
        } catch (Exception e) {
        	STD.printStackTrace(e);
        	bRet = false;
        }
        
        return bRet;
	}
	public boolean	LoadFreeNPCs() {
		boolean		bRet = false;
        byte[] buf = CConFile.read(FREENPCS_FILE);
        if (buf == null)
            return false;
        
        try {
            ByteArrayInputStream baos = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(baos);

            for (int i = 0; i < COUNTRY_MAX; i++) {
            	m_bFreeNPCs[i] = dis.readBoolean();
            }

            dis.close();
            baos.close();
            
            bRet = true;
        }
        catch (Exception e) {
        	STD.printStackTrace(e);
            bRet = false;
        }
        
		return bRet;
	}
	public boolean	CheckData(String szFileName) {
		return true;
	}

	public void	SetContinueDataToGlobal(SaveData pData) {
		if (pData == null)
			pData = m_stContinueData;
		SetUserCountry(pData.nUserCountry);	
		m_nUserSoldiers = pData.nUserSoldiers;
		m_nNPCCountry = pData.nNPCCountry;
		SetNPCInfo(m_nNPCCountry);
		
		for (int i = 0; i < COUNTRY_MAX; i ++)
			m_bCountryState[i] = pData.bCountryState[i];
//		m_bBGM = pData.bBGM;
//		m_bSE = pData.bSE;
	}
	public void	SetContinueDataFromGlobal(SaveData pData) {
		if (pData == null)
			pData = m_stContinueData;
		pData.nUserCountry = m_nUserCountry;
		pData.nUserSoldiers = m_nUserSoldiers;
		pData.nNPCCountry = m_nNPCCountry;
		
		for (int i = 0; i < COUNTRY_MAX; i ++)
			pData.bCountryState[i] = m_bCountryState[i];
		pData.bBGM = m_bBGM;
		pData.bSE = m_bSE;
	}
	
	public int		GetGameMode() {
		return m_nGameMode;
	}
	public void 	SetGameMode(int nMode) {
		if (nMode < GM_UNIFY || nMode > GM_FREE)
			nMode = GM_UNIFY;
		m_nGameMode = nMode;
	}
	
	public void 	InitPlayerCountry() {
		SetUserCountry(_XUCHANG);	
		m_nNPCCountry = _LUOYANG;
		SetNPCInfo(m_nNPCCountry);	
	}
	public int		GetNPCCountry()	{
		return m_nNPCCountry;
	}
	public void	SetNPCCountry(int nCountry) {
		nCountry = max(nCountry, _PINGYUAN);	
		nCountry = min(nCountry, _XIHAI);
		m_nNPCCountry = nCountry;
		SetNPCInfo(nCountry);
	}
	public int		GetNPCChr()	{
		return m_nNPCCharacter;
	}
	public void	SetNPCChr(int nChr) {
		nChr = max(nChr, CHR_CAOCAO);	
		nChr = min(nChr, CHR_SOKYOU);
		
		m_nNPCCharacter = nChr;
		for (int i = 0; i < 4; i ++)
			m_nNCPChrs[i] = g_nSoldierConfig[nChr][i];
	}
	public void 	SetNPCInfo(int nCountry) {
		COUNTRY_INFO info;
		info = GetCountryInfo(nCountry);
		SetNPCChr(info.nCharacter);	
	}
	
	public int		GetUserCountry() {
		return m_nUserCountry;
	}
	public void	SetUserCountry(int nCountry) {
		nCountry = max(nCountry, _PINGYUAN);	
		nCountry = min(nCountry, _XIHAI);
		m_nUserCountry = nCountry;
		SetUserInfo(m_nUserCountry);
	}
	public int 	GetUserChr() {
		return m_nUserCharacter;
	}
	public void 	SetUserChr(int nChr) {
		nChr = max(nChr, CHR_CAOCAO);	
		nChr = min(nChr, CHR_SOKYOU);
		m_nUserCharacter = nChr;
	}
	public void 	SetUserInfo(int nCountry) {
		COUNTRY_INFO info;
		info = GetCountryInfo(nCountry);
		SetUserChr(info.nCharacter);
		SetUserSoldiers(info.nSoldiers);
	}
	
	public int		GetUserSoldiers() {
		return m_nUserSoldiers;
	}
	public void 	SetUserSoldiers(int nSoldiers) {
		nSoldiers = min(nSoldiers, MAX_SOLDIERS);
		nSoldiers = max(nSoldiers, 0);	
		m_nUserSoldiers = nSoldiers;
	}
	public void 	UpdateUserSoldiers(boolean bWin) {
		int nSoldiers = m_nUserSoldiers;
		if (bWin)
			nSoldiers += WIN_SOLDIERS;
		else
			nSoldiers += LOSE_SOLDIERS;
		
		nSoldiers = min(nSoldiers, MAX_SOLDIERS);
		nSoldiers = max(nSoldiers, 0);
		m_nUserSoldiers = nSoldiers;
	}
	
	public boolean	IsCaptureCountry(int nCountry) {
		return m_bCountryState[nCountry];
	}
	public void 	SetCaptureCountry(int nCountry, boolean bCapture) {
		m_bCountryState[nCountry] = bCapture;
	}
	public void 	InitCountryState() {
		STD.MEMSET(m_bCountryState, false);
	}
	
	public boolean 	IsCaptureFreeNPC(int nCountry) {
		if (m_bFreeNPCs[nCountry])
			return true;
		COUNTRY_INFO info = GetCountryInfo(nCountry);
		int nCharacter = info.nCharacter;
		if (nCharacter == CHR_SHANZEI || nCharacter == CHR_BINGSHI || nCharacter == CHR_JINWEIBING)
			return true;
		else
			return false;
	}
	public void	SetCaptureFreeNPC(int nCountry) {
		m_bFreeNPCs[nCountry] = true;
	}
	public void 	InitFreeNPCs() {
		STD.MEMSET(m_bFreeNPCs, false);
	}
	
	public boolean	GetBGM() {
		return m_bBGM;
	}
	public void	SetBGM(boolean bBGM) {
		m_bBGM = bBGM;
	}

	public boolean	GetSE() {
		return m_bSE;
	}
	public void	SetSE(boolean bSE) {
		m_bSE = bSE;
	}
	
	public void 		InitGameRule() {
		m_stGameRule.Init();
	}
	
	public RULE_INFO	GetGameRule() {
		return m_stGameRule;
	}
	public boolean		GetGameRuleVal(int nId) {
		return m_stGameRule.getVals(nId);
	}
	public void		SetGameRule(RULE_INFO pRule) {
		if (pRule != null)
			m_stGameRule.Copy(pRule);
	}
	public void		SetGameRuleVal(int nId, boolean bVal) {
		m_stGameRule.setVals(nId, bVal);
	}

	public RESULT_INFO	GetGameResult() {
		return m_stGameResult;
	}
	public int			GetGameResultVal(int nId) {
		return m_stGameResult.getVals(nId);
	}
	public void		SetGameResult(RESULT_INFO pResult) {
		if (pResult != null)
			m_stGameResult.Copy(pResult);
	}
	public void		SetGameResultVal(int nId, int bVal) {
		m_stGameResult.setVals(nId, bVal);
	}
	public void		AddGameResultVal(int nId, int nAdd) {
		int nVal = m_stGameResult.getVals(nId);
		nVal += nAdd;
		m_stGameResult.setVals(nId, min(nVal, MAX_LIMIT));
	}
	public void		InitGameResult() {
		m_stGameResult.Init();	
	}
	
	public int[]	GetNPCs() {
		return m_nNCPChrs;
	}
	public int		GetNPCs(int nIndex)  {
		return m_nNCPChrs[nIndex];
	}
	public void		SetNPCs(int[] nNpc) {
		if (nNpc != null)
			STD.MEMCPY(m_nNCPChrs, nNpc);
	}
	public void		SetNPCs(int nIndex, int nNPCChr) {
		m_nNCPChrs[nIndex] = nNPCChr;
	}
	
	public boolean IsContinueGame() {
		return m_bContinueGame;
	}
	public void SetContinueGame(boolean bContinue) {
		m_bContinueGame = bContinue;
	}

	public static CPoint getCountryPos(int nCountry) {
		int x[] = { 
			360, 448, 448, 448, 448, 516, 512, 512, 372, 268, 272, 268, 268, 
			332, 400, 268, 332, 336, 164, 160, 104, 108, 112,  96, 100,  36
		};
		int y[] = {
			496, 496, 560, 632, 696, 636, 712, 796, 632, 632, 552, 692, 748,
			744, 744, 804, 804, 888, 640, 724, 724, 800, 864, 640, 580, 580
		};
		return new CPoint(x[nCountry]/2, y[nCountry]/2);
	}















	///////////////////////////////// Global Data /////////////////////////////
	public static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	public static int min(int a, int b) {	
		return (a > b) ? b : a;
	}
	
	public static CHARATER_INFO GetCharacterInfo(int nChrNo) {
		final CHARATER_INFO chrInfo[] = new CHARATER_INFO [] 
		{	//                初期治める国,  攻撃,知恵, 思考No, レベルNo
			new CHARATER_INFO(_XUCHANG,     75, 80, 8, 5),		//曹操孟徳
			new CHARATER_INFO(_CHANGDU,     60, 60, 5, 3),		//劉備玄徳
			new CHARATER_INFO(_OH,          55, 70, 4, 3),		//孫権仲謀
			new CHARATER_INFO(_BOHAI,       50, 45, 4, 2),		//袁紹本初
			new CHARATER_INFO(_BEIPING,     85, 70, 4, 3),		//趙雲子龍
			new CHARATER_INFO(_JIANGXIA,    90, 60, 7, 4),		//関羽雲長
			new CHARATER_INFO(_XIAPI,       95, 45, 3, 2),		//張飛益徳
			new CHARATER_INFO(_ZHENLIU,    100, 20, 1, 1),		//呂布奉先
			new CHARATER_INFO(_LUOYANG,     90, 75, 4, 4),		//夏侯惇元譲
			new CHARATER_INFO(_JINCHENG,    80, 60, 2, 2),		//董卓仲穎
			new CHARATER_INFO(_XIANGYANG,   50,100, 8, 5),		//諸葛亮孔明
			new CHARATER_INFO(_HUIJI,       70, 90, 6, 4),		//周瑜公瑾
			new CHARATER_INFO(COUNTRY_NONE, 25, 10, 1, 1),		//山賊
			new CHARATER_INFO(COUNTRY_NONE, 35, 30, 2, 1),		//兵士
			new CHARATER_INFO(COUNTRY_NONE, 50, 30, 4, 2),		//近衛兵
			new CHARATER_INFO(_CHANGAN,     30, 70, 1, 1),		//貂蝉
			new CHARATER_INFO(_GUIYANG,     45, 60, 3, 3),		//小喬
		};
		if (nChrNo < 0)
			nChrNo = 0;
		else if (nChrNo >= CHR_MAX)
			nChrNo = CHR_MAX;
		
		return chrInfo[nChrNo];
	}

	public static COUNTRY_INFO GetCountryInfo(int nCountryNo) {
		final COUNTRY_INFO countryInfo[] = new COUNTRY_INFO[] 
		{//兵力, 初期治める人物
			new COUNTRY_INFO(3000, CHR_SHANZEI),	//平原
			new COUNTRY_INFO(3000, CHR_CHOUN),		//北平
			new COUNTRY_INFO(8000, CHR_YUANSHAO),	//勃海
			new COUNTRY_INFO(2000, CHR_LYOPO),		//陳留
			new COUNTRY_INFO(7000, CHR_SHANZEI),	//汝南
			new COUNTRY_INFO(2000, CHR_ZHANGBI),	//下?
			new COUNTRY_INFO(6000, CHR_SUNQUAN),	//呉
			new COUNTRY_INFO(3000, CHR_JUYOU),		//会稽
			new COUNTRY_INFO(5000, CHR_CAOCAO),		//許昌
			new COUNTRY_INFO(4000, CHR_HAHUDON),	//洛陽 - 10
			new COUNTRY_INFO(4000, CHR_SHANZEI),	//平陽
			new COUNTRY_INFO(5000, CHR_JIEKALYANG),	//襄陽
			new COUNTRY_INFO(7000, CHR_BINGSHI),	//武陵
			new COUNTRY_INFO(6000, CHR_JINWEIBING),	//長沙
			new COUNTRY_INFO(4000, CHR_GUANWU),		//江夏
			new COUNTRY_INFO(6000, CHR_BINGSHI),	//零陵
			new COUNTRY_INFO(6000, CHR_SOKYOU),		//桂陽
			new COUNTRY_INFO(5000, CHR_SHANZEI),	//南海
			new COUNTRY_INFO(8000, CHR_CHOSUN),		//長安
			new COUNTRY_INFO(8000, CHR_JINWEIBING),	//漢中 - 20
			new COUNTRY_INFO(5000, CHR_LIUBI),		//成都
			new COUNTRY_INFO(3000, CHR_BINGSHI),	//建寧
			new COUNTRY_INFO(3000, CHR_SHANZEI),	//雲南
			new COUNTRY_INFO(5000, CHR_JINWEIBING),	//武都
			new COUNTRY_INFO(8000, CHR_DONGTAK),	//金城
			new COUNTRY_INFO(3000, CHR_SHANZEI),	//西海
		};
		if (nCountryNo < 0)
			nCountryNo = 0;
		else if (nCountryNo >= COUNTRY_MAX)
			nCountryNo = COUNTRY_MAX-1;
		
		return countryInfo[nCountryNo];
	}

	public static String GetCountryName(int nCountryNo) {
		String szCountry[] = new String[] 
		{
			"平原",	"北平",	"勃海",	"陳留", "汝南",
			"下邳",	"呉",	"会稽",	"許昌",	"洛陽",
			"平陽",	"襄陽",	"武陵",	"長沙",	"江夏",
			"零陵",	"桂陽",	"南海",	"長安",	"漢中",
			"成都",	"建寧",	"雲南",	"武都",	"金城",	"西海"
		};
		return szCountry[nCountryNo];
	}

	public static RULE_INFO GetCountryRuleInfo(int nCountryNo) {
		final RULE_INFO ruleInfo[] = new RULE_INFO[] 
		{				//革命,      都落ち,  スペ３,  しばり,   8切り,  ジョーカー,2上がり禁止
			new RULE_INFO(false,	false,	false,	false,	false,	false,	false),	//平原
			new RULE_INFO(false,	false,	true,	true,	true,	false,	false),	//北平
			new RULE_INFO(false,	true,	false,	true,	true,	false,	false),	//勃海
			new RULE_INFO(true,		true,	true,	true,	true,	true,	true),	//陳留
			new RULE_INFO(true,		false,	false,	false,	false,	false,	false),	//汝南
			new RULE_INFO(false,	false,	false,	false,	true,	false,	false),	//下?
			new RULE_INFO(true,		true,	true,	true,	true,	true,	true),	//呉
			new RULE_INFO(false,	false,	false,	true,	true,	true,	true),	//会稽
			new RULE_INFO(true,		true,	false,	true,	true,	false,	true),	//許昌
			new RULE_INFO(true,		true,	false,	false,	false,	true,	true),	//洛陽 - 10
			new RULE_INFO(true,		false,	true,	true,	false,	true,	true),	//平陽
			new RULE_INFO(true,		true,	false,	true,	true,	true,	false),	//襄陽
			new RULE_INFO(false,	false,	false,	false,	false,	false,	false),	//武陵
			new RULE_INFO(true,		false,	true,	true,	false,	true,	true),	//長沙
			new RULE_INFO(false,	false,	false,	true,	true,	true,	false),	//江夏
			new RULE_INFO(false,	false,	true,	false,	false,	true,	false),	//零陵
			new RULE_INFO(false,	true,	false,	false,	false,	true,	true),	//桂陽
			new RULE_INFO(false,	true,	true,	true,	true,	true,	true),	//南海
			new RULE_INFO(true,		true,	true,	true,	true,	true,	true),	//長安
			new RULE_INFO(false,	false,	false,	false,	false,	false,	false),	//漢中 - 20
			new RULE_INFO(true,		true,	true,	false,	true,	false,	false),	//成都
			new RULE_INFO(true,		false,	false,	false,	true,	false,	false),	//建寧
			new RULE_INFO(true,		false,	false,	false,	true,	false,	false),	//雲南
			new RULE_INFO(false,	false,	false,	false,	false,	false,	false),	//武都
			new RULE_INFO(true,		true,	true,	true,	true,	true,	true),	//金城
			new RULE_INFO(false,	false,	false,	false,	false,	false,	false),	//西海
		};
		if (nCountryNo < 0)
			nCountryNo = 0;
		else if (nCountryNo >= COUNTRY_MAX)
			nCountryNo = COUNTRY_MAX-1;
		
		return ruleInfo[nCountryNo];
	}

	public static int GetAdjoinCountry(int nCurrentCountry, int nDirect)
	{
		int nCountry = COUNTRY_NONE;
		nCurrentCountry += 1;
		switch (nCurrentCountry)
		{
		case 1:
			if (nDirect == DIRECT_RIGHT)
				nCountry = 2;
			break;
		case 2:
			if (nDirect == DIRECT_LEFT)
				nCountry = 1;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 3;
			break;
		case 3:
			if (nDirect == DIRECT_UP)
				nCountry = 2;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 4;
			break;
		case 4:
			if (nDirect == DIRECT_LEFT)
				nCountry = 9;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 6;
			if (nDirect == DIRECT_UP)
				nCountry = 3;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 5;
			break;
		case 5:
			if (nDirect == DIRECT_UP)
				nCountry = 4;
			break;
		case 6:
			if (nDirect == DIRECT_LEFT)
				nCountry = 4;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 7;
			break;
		case 7:
			if (nDirect == DIRECT_UP)
				nCountry = 6;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 8;
			break;
		case 8:
			if (nDirect == DIRECT_UP)
				nCountry = 7;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 17;
			break;
		case 9:
			if (nDirect == DIRECT_LEFT)
				nCountry = 10;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 4;
			break;
		case 10:
			if (nDirect == DIRECT_UP)
				nCountry = 11;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 12;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 19;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 9;
			break;
		case 11:
			if (nDirect == DIRECT_DOWN)
				nCountry = 10;
			break;
		case 12:
			if (nDirect == DIRECT_UP)
				nCountry = 10;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 13;
			break;
		case 13:
			if (nDirect == DIRECT_UP)
				nCountry = 12;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 16;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 14;
			break;
		case 14:
			if (nDirect == DIRECT_DOWN)
				nCountry = 17;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 13;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 15;
			break;
		case 15:
			if (nDirect == DIRECT_LEFT)
				nCountry = 14;
			break;
		case 16:
			if (nDirect == DIRECT_UP)
				nCountry = 13;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 22;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 17;
			break;
		case 17:
			if (nDirect == DIRECT_UP)
				nCountry = 14;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 18;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 16;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 8;
			break;
		case 18:
			if (nDirect == DIRECT_UP)
				nCountry = 17;
			break;
		case 19:
			if (nDirect == DIRECT_DOWN)
				nCountry = 20;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 24;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 10;
			break;
		case 20:
			if (nDirect == DIRECT_UP)
				nCountry = 19;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 21;
			break;
		case 21:
			if (nDirect == DIRECT_DOWN)
				nCountry = 22;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 20;
			break;
		case 22:
			if (nDirect == DIRECT_UP)
				nCountry = 21;
			else if (nDirect == DIRECT_DOWN)
				nCountry = 23;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 16;
			break;
		case 23:
			if (nDirect == DIRECT_UP)
				nCountry = 22;
			break;
		case 24:
			if (nDirect == DIRECT_UP)
				nCountry = 25;
			else if (nDirect == DIRECT_RIGHT)
				nCountry = 19;
			break;
		case 25:
			if (nDirect == DIRECT_DOWN)
				nCountry = 24;
			else if (nDirect == DIRECT_LEFT)
				nCountry = 26;
			break;
		case 26:
			if (nDirect == DIRECT_RIGHT)
				nCountry = 25;
			break;
		}
		if (nCountry != COUNTRY_NONE)
			nCountry -= 1;
		return nCountry;
	}
	
	public static void playSEEx(int se) {
//		Globals.stopBGM();
//		playSE(se);
		
		g_Sound.playBGM(se, false);
	}
	public static void playSE(int se) {
		if (m_bSE == true)
			g_Sound.playSE(se);
	}
	public static void stopSEEx() {
		g_Sound.stopSE();
	}
	public static void stopSE(int se) {
		g_Sound.stopSE();
	}
	public static void playBGM(int bgm) {
		if ( m_bBGM == true )
			g_Sound.playBGM(bgm);
		else
			g_Sound.stopBGM();
	}
	public static void resumeBGM() {
		g_Sound.resumeBGM();
	}
	public static void pauseBGM() {
		g_Sound.pauseBGM();
	}
	public static void stopBGM() {
		g_Sound.stopBGM();
	}
}
