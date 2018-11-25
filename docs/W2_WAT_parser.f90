!c control_file_read_write.f90

!***********************************************************************************************************************************
!**                                                      Module Declaration                                                       **
!***********************************************************************************************************************************
MODULE PREC
  INTEGER, PARAMETER :: I2=SELECTED_INT_KIND (3)
  INTEGER, PARAMETER :: R8=SELECTED_REAL_KIND(15)
END MODULE PREC
MODULE RSTART
  USE PREC
  REAL                                               :: DLTS,   CURMAX, DLTFF, DLTMAXX    ! SW 7/13/2010
  INTEGER                                            :: RSODP,  DLTDP,  TSRDP,  WDODP,  CUF,    RSO=31
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: SNPDP,  VPLDP,  CPLDP,  PRFDP,  SCRDP,  SPRDP,  FLXDP, NSPRF
  REAL                                               :: NXTMRS, NXTMWD, NXTMTS
  REAL,              ALLOCATABLE, DIMENSION(:)       :: NXTMSN, NXTMPR, NXTMSP, NXTMCP, NXTMVP, NXTMSC, NXTMFL
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: SBKT,   ELTMF
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: TSSUH2, TSSDH2, SAVH2,  SAVHR,  SU,     SW,     SAZ
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:,:)   :: CSSUH2, CSSDH2
  REAL(R8)                                           :: ELTM
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: VOLIN,  VOLOUT, VOLUH,  VOLDH,  VOLPR,  VOLTRB, VOLDT, VOLWD,  VOLEV
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: VOLSBR, VOLTBR, VOLSR,  VOLTR
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: TSSEV,  TSSPR,  TSSTR,  TSSDT,  TSSWD,  TSSUH,  TSSDH, TSSIN,  TSSOUT
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: TSSS,   TSSB,   TSSICE
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: ESBR,   ETBR,   EBRI,   SZ
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: CMBRT
END MODULE RSTART
MODULE GLOBAL
  USE PREC
  REAL,   PARAMETER                                  :: DAY=86400.0,  NONZERO=1.0E-20, REFL=0.94, FRAZDZ=0.14, DZMIN=1.4E-7
  REAL,   PARAMETER                                  :: AZMIN=1.4E-6, DZMAX=1.0E3,     RHOW=1000.0
  REAL                                               :: DLT,    DLTMIN, DLTTVD
  REAL                                               :: BETABR, START,  HMAX2,  CURRENT
  REAL(R8),   POINTER,            DIMENSION(:,:)     :: U,      W,      T2,     AZ,     RHO,    ST,     SB
  REAL(R8),   POINTER,            DIMENSION(:,:)     :: DLTLIM, VSH,    ADMX,   DM,     ADMZ,   HDG,    HPG,    GRAV
  REAL(R8),   TARGET,ALLOCATABLE, DIMENSION(:,:)     :: T1,     TSS
  REAL(R8),   TARGET,ALLOCATABLE, DIMENSION(:,:,:)   :: C1,     C2,     C1S,    CSSB,   CSSK
  REAL,       TARGET,ALLOCATABLE, DIMENSION(:,:,:)   :: KF,     CD
  REAL(R8),   TARGET,ALLOCATABLE, DIMENSION(:,:,:)   :: HYD
  REAL,   TARGET,    ALLOCATABLE, DIMENSION(:,:,:,:) :: AF,     EF
  REAL,              ALLOCATABLE, DIMENSION(:)       :: ICETH,  ELKT,   HMULT,  CMULT,  CDMULT, WIND2,  AZMAX,  PALT, Z0
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: QSS,    VOLUH2, VOLDH2, QUH1,   QDH1,   UXBR,   UYBR,   VOL
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: ALLIM,  APLIM,  ANLIM,  ASLIM,  KFS
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: ELLIM,  EPLIM,  ENLIM,  ESLIM
  INTEGER                                            :: W2ERR,  WRN
  INTEGER                                            :: IMX,    KMX,    NBR,    NTR,    NWD,    NWB,    NCT,    NBOD
  INTEGER                                            :: NST,    NSP,    NGT,    NPI,    NPU,    NWDO,   NIKTSR, NUNIT
  INTEGER                                            :: JW,     JB,     JC,     IU,     ID,     KT,     I,      JJB
  INTEGER                                            :: NOD,    NDC,    NAL,    NSS,    NHY,    NFL,    NEP,    NEPT
  INTEGER                                            :: NZP,    nzpt, JZ,     NZOOS,  NZOOE,  nmc,   nmct  ! number of zooplankton groups, CONSTIUENT NUMBER FOR ZOOPLANKTON, START AND END
  INTEGER, POINTER,               DIMENSION(:)       :: SNP,    PRF,    VPL,    CPL,    SPR,    FLX,    FLX2
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: BS,     BE,     US,     CUS,    DS,     JBDN
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: KB,     KTI,    SKTI,   KTWB,   KBMIN,  CDHS
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: UHS,    DHS,    UQB,    DQB
  INTEGER, TARGET,   ALLOCATABLE, DIMENSION(:,:)     :: OPT
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: nbodc, nbodn, nbodp        ! cb 6/6/10
  LOGICAL,           ALLOCATABLE, DIMENSION(:)       :: ICE,    ICE_CALC,LAYERCHANGE
  CHARACTER(10)                                      :: CCTIME
  CHARACTER(12)                                      :: CDATE
  CHARACTER(72)                                      :: RSIFN
  CHARACTER(180)                                     :: moddir                     ! current working directory
  REAL(R8),     SAVE, ALLOCATABLE, DIMENSION(:,:)    :: RATZ,   CURZ1,  CURZ2,  CURZ3    ! SW 5/15/06
  real                                               :: g,pi
  REAL(R8)                                           :: DENSITY
!  DATA                                        NDC /23/, NHY /15/, NFL /118/
  DATA                                        NDC /23/, NHY /15/, NFL /121/      ! cb 6/6/10
  DATA                                        G /9.81/, PI/3.14159265359/
  DATA                                        WRN /32/, W2ERR /33/
  EXTERNAL DENSITY
END MODULE GLOBAL
MODULE GEOMC
  USE PREC
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: JBUH,   JBDH,   JWUH,   JWDH
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: ALPHA,  SINA,   COSA,   SLOPE,  BKT,    DLX,    DLXR, SLOPEC, SINAC
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: H,      H1,     H2,     BH1,    BH2,    BHR1,    BHR2,   AVHR
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: B,      BI,     BB,     BH,     BHR,    BR,      EL,     AVH1,  AVH2, bnew ! SW 1/23/06
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: DEPTHB, DEPTHM, FETCHU, FETCHD
  REAL(R8),          ALLOCATABLE, DIMENSION(:)       :: Z, ELWS
END MODULE GEOMC
MODULE NAMESC
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: LNAME
  CHARACTER(6),      ALLOCATABLE, DIMENSION(:)       :: CUNIT,  CUNIT2
  CHARACTER(8),      ALLOCATABLE, DIMENSION(:)       :: CNAME2, CDNAME2
  CHARACTER(9),      ALLOCATABLE, DIMENSION(:)       :: FMTH,   FMTC,   FMTCD
  CHARACTER(19),     ALLOCATABLE, DIMENSION(:)       :: CNAME1
  CHARACTER(43),     ALLOCATABLE, DIMENSION(:)       :: CNAME,  CNAME3, CDNAME, CDNAME3, HNAME
  CHARACTER(72),     ALLOCATABLE, DIMENSION(:)       :: TITLE
  CHARACTER(10),     ALLOCATABLE, DIMENSION(:,:)     :: CONV
END MODULE NAMESC
MODULE STRUCTURES
  REAL                                               :: DIA,    FMAN,   CLEN,   CLOSS,  UPIE,   DNIE
  REAL,              ALLOCATABLE, DIMENSION(:)       :: QOLD,   QOLDS,  VMAX,   DTP,    DTPS
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EGT,    A1GT,   B1GT,   G1GT,   A2GT,   B2GT,   G2GT, EGT2
  REAL,              ALLOCATABLE, DIMENSION(:)       :: QGT,    GTA1,   GTB1,   GTA2,   GTB2,   BGT
  REAL,              ALLOCATABLE, DIMENSION(:)       :: QSP,    A1SP,   B1SP,   A2SP,   B2SP,   ESP
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EUPI,   EDPI,   WPI,    DLXPI,  FPI,    FMINPI, QPI, BP
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: YS,     VS,     YSS,    VSS,    YST,    VST,    YSTS,   VSTS
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: IUPI,   IDPI,   JWUPI,  JWDPI,  JBDPI,  JBUPI
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: IUSP,   IDSP,   JWUSP,  JWDSP,  JBUSP,  JBDSP
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: IUGT,   IDGT,   JWUGT,  JWDGT,  JBUGT,  JBDGT
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: IWR,    KTWR,   KBWR
  LOGICAL,           ALLOCATABLE, DIMENSION(:)       :: LATERAL_SPILLWAY, LATERAL_PIPE, LATERAL_GATE, LATERAL_PUMP, BEGIN, WLFLAG
  CHARACTER(8),      ALLOCATABLE, DIMENSION(:)       :: LATGTC, LATSPC, LATPIC, LATPUC, DYNGTC, DYNPIPE, DYNPUMP                         ! SW 5/10/10
  CHARACTER(8)                                       :: GT2CHAR 
  REAL,          ALLOCATABLE, DIMENSION(:)     :: EPU,    STRTPU, ENDPU,  EONPU,  EOFFPU, QPU
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: IUPU,   IDPU,   KTPU,   KBPU,   JWUPU,  JWDPU,  JBUPU,  JBDPU
  real :: THR, OMEGA, EPS2
  integer :: NN, NNPIPE, NC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EGTo,bgto       ! cb/8/13/ 2010
  CHARACTER(8),      ALLOCATABLE, DIMENSION(:)       :: gtic            ! cb/8/13/ 2010
  DATA                                             THR/0.01/, OMEGA/0.8/, EPS2/0.0001/
  DATA                                          NN/19/ ,   NNPIPE /19/, NC/7/
END MODULE STRUCTURES
MODULE TRANS
  USE PREC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: THETA
  REAL(R8),POINTER,               DIMENSION(:,:)     :: COLD,   CNEW,   SSB,    SSK
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: DX,     DZ,     DZQ
  REAL(R8),          ALLOCATABLE, DIMENSION(:,:)     :: ADX,    ADZ,    AT,     VT,     CT,     DT
END MODULE TRANS
MODULE SURFHE
  REAL                                               :: RHOWCP, PHISET
  REAL,              ALLOCATABLE, DIMENSION(:)       :: ET,     CSHE,   LAT,    LONGIT, SHADE,  RB,     RE,     RC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: WIND,   WINDH,  WSC,    AFW,    BFW,    CFW,    PHI0
  LOGICAL,           ALLOCATABLE, DIMENSION(:)       :: RH_EVAP
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: IWIND  !MLM 08/12/05
END MODULE SURFHE
MODULE TVDC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: QIN,    QTR,    QDTR,   PR,     ELUH,   ELDH,   QWD,    QSUM
  REAL,              ALLOCATABLE, DIMENSION(:)       :: TIN,    TTR,    TDTR,   TPR,    TOUT,   TWDO,   TIND,   QIND
  REAL,              ALLOCATABLE, DIMENSION(:)       :: TAIR,   TDEW,   CLOUD,  PHI,    SRON
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: CIN,    CTR,    CDTR,   CPR,    CIND,   TUH,    TDH,    QOUT
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: CUH,    CDH
  INTEGER                                            :: NAC,    NOPEN
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: NACPR,  NACIN,  NACDT,  NACTR,  NACD,   CN
  INTEGER,           ALLOCATABLE, DIMENSION(:,:)     :: TRCN,   INCN,   DTCN,   PRCN
  LOGICAL                                            :: CONSTITUENTS
  CHARACTER(72)                                      :: QGTFN,  QWDFN,  WSCFN,  SHDFN
  CHARACTER(72),     ALLOCATABLE, DIMENSION(:)       :: METFN,  QOTFN,  QINFN,  TINFN,  CINFN,  QTRFN,  TTRFN,  CTRFN,  QDTFN
  CHARACTER(72),     ALLOCATABLE, DIMENSION(:)       :: TDTFN,  CDTFN,  PREFN,  TPRFN,  CPRFN,  EUHFN,  TUHFN,  CUHFN,  EDHFN
  CHARACTER(72),     ALLOCATABLE, DIMENSION(:)       :: EXTFN,  CDHFN,  TDHFN
END MODULE TVDC
MODULE KINETIC
  USE PREC
  REAL                                               :: kdo                        !v3.5
  REAL(R8),    POINTER,               DIMENSION(:,:)     :: TDS,    COL,    NH4,    NO3,    PO4,    FE,     DSI,    PSI,    LDOM
  REAL(R8),    POINTER,               DIMENSION(:,:)     :: RDOM,   LPOM,   RPOM,   O2,     TIC,    ALK
  REAL(R8),    POINTER,               DIMENSION(:,:)     :: COLSS,  NH4SS,  NO3SS,  PO4SS,  FESS,   DSISS,  PSISS,  LDOMSS
  REAL(R8),    POINTER,               DIMENSION(:,:)     :: RDOMSS, LPOMSS, RPOMSS, DOSS,   TICSS,  CASS
  REAL,    POINTER,               DIMENSION(:,:)     :: PH,     CO2,    HCO3,   CO3
  REAL,    POINTER,               DIMENSION(:,:)     :: TN,     TP,     TKN
  REAL,    POINTER,               DIMENSION(:,:)     :: DON,    DOP,    DOC
  REAL,    POINTER,               DIMENSION(:,:)     :: PON,    POP,    POC
  REAL,    POINTER,               DIMENSION(:,:)     :: TON,    TOP,    TOC
  REAL,    POINTER,               DIMENSION(:,:)     :: APR,    CHLA,   ATOT
  REAL,    POINTER,               DIMENSION(:,:)     :: O2DG
  REAL,    POINTER,               DIMENSION(:,:)     :: SSSI,   SSSO,   TISS,   TOTSS
  REAL,    POINTER,               DIMENSION(:,:)     :: PO4AR,  PO4AG,  PO4AP,  PO4SD,  PO4SR,  PO4NS,  PO4POM, PO4DOM, PO4OM
  REAL,    POINTER,               DIMENSION(:,:)     :: PO4ER,  PO4EG,  PO4EP,  TICEP,  DOEP,   DOER
  REAL,    POINTER,               DIMENSION(:,:)     :: NH4ER,  NH4EG,  NH4EP,  NO3EG,  DSIEG,  LDOMEP, LPOMEP
  REAL,    POINTER,               DIMENSION(:,:)     :: NH4AR,  NH4AG,  NH4AP,  NH4SD,  NH4SR,  NH4D,   NH4POM, NH4DOM, NH4OM
  REAL,    POINTER,               DIMENSION(:,:)     :: NO3AG,  NO3D,   NO3SED
  REAL,    POINTER,               DIMENSION(:,:)     :: DSIAG,  DSID,   DSISD,  DSISR,  DSIS
  REAL,    POINTER,               DIMENSION(:,:)     :: PSIAM,  PSID,   PSINS
  REAL,    POINTER,               DIMENSION(:,:)     :: FENS,   FESR
  REAL,    POINTER,               DIMENSION(:,:)     :: LDOMAP, LDOMD,  LRDOMD, RDOMD
  REAL,    POINTER,               DIMENSION(:,:)     :: LPOMAP, LPOMD,  LRPOMD, RPOMD,  LPOMNS, RPOMNS
  REAL,    POINTER,               DIMENSION(:,:)     :: DOAP,   DOAR,   DODOM,  DOPOM,  DOOM,   DONIT
  REAL,    POINTER,               DIMENSION(:,:)     :: DOSED,  DOSOD,  DOBOD,  DOAE
  REAL,    POINTER,               DIMENSION(:,:)     :: CBODU,  CBODDK, TICAP
  REAL,    POINTER,               DIMENSION(:,:)     :: SEDD,   SODD,   SEDAS,  SEDOMS, SEDNS
  REAL(R8),POINTER,               DIMENSION(:,:,:)   :: SS,     ALG,    CBOD,   CG
  REAL(R8),POINTER,               DIMENSION(:,:,:)   :: SSSS,   ASS,    CBODSS, CGSS
  REAL,    POINTER,               DIMENSION(:,:,:)   :: AGR,    ARR,    AER,    AMR,    ASR
  REAL,    POINTER,               DIMENSION(:,:,:)   :: EGR,    ERR,    EER,    EMR,    EBR
  REAL(R8),POINTER,               DIMENSION(:,:)     :: LDOMP,  RDOMP,  LPOMP,  RPOMP,  LDOMN,  RDOMN,  LPOMN,  RPOMN
  REAL(R8),POINTER,               DIMENSION(:,:)     :: LDOMPSS,  RDOMPSS, LPOMPSS, RPOMPSS, LDOMNSS, RDOMNSS
  REAL(R8),POINTER,               DIMENSION(:,:)     :: LPOMNSS,  RPOMNSS
  REAL,    POINTER,               DIMENSION(:,:)     :: LDOMPAP,  LDOMPEP, LPOMPAP, LPOMPNS, RPOMPNS
  REAL,    POINTER,               DIMENSION(:,:)     :: LDOMNAP,  LDOMNEP, LPOMNAP, LPOMNNS, RPOMNNS
  REAL,    POINTER,               DIMENSION(:,:)     :: SEDDP,    SEDASP,  SEDOMSP, SEDNSP,  LPOMEPP
  REAL,    POINTER,               DIMENSION(:,:)     :: SEDDN,    SEDASN,  SEDOMSN, SEDNSN,  LPOMEPN, SEDNO3
  REAL,    POINTER,               DIMENSION(:,:)     :: SEDDC,    SEDASC,  SEDOMSC, SEDNSC,  LPOMEPC
  REAL,    POINTER,               DIMENSION(:,:)     :: CBODNS,   SEDCB,   SEDCBP,  SEDCBN,  SEDCBC
  REAL,    POINTER,               DIMENSION(:,:)     :: sedbr,    sedbrp,  sedbrc,  sedbrn, co2reaer        !cb 11/30/06
  REAL(R8),POINTER,               DIMENSION(:,:,:)   :: cbodp,    cbodn       ! cb 6/6/10
  REAL(R8),POINTER,               DIMENSION(:,:,:)   :: cbodpss,    cbodnss       ! cb 6/6/10
  REAL,    POINTER,               DIMENSION(:,:)     :: CBODNSp,  CBODNSn          ! cb 6/6/10
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: EPM,    EPD,    EPC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: CGQ10,  CG0DK,  CG1DK,  CGS, CGLDK, CGKLF, CGCS  !LCJ 2/26/15 SW 10/16/15
  REAL,              ALLOCATABLE, DIMENSION(:)       :: SOD,    SDK,    LPOMDK, RPOMDK, LDOMDK, RDOMDK, LRDDK,  LRPDK
  REAL,              ALLOCATABLE, DIMENSION(:)       :: SSS,    TAUCR,  POMS,   FES, seds, sedb  !cb 11/27/06
  REAL,              ALLOCATABLE, DIMENSION(:)       :: AG,     AR,     AE,     AM,     AS,     AHSN,   AHSP,   AHSSI,  ASAT
  REAL,              ALLOCATABLE, DIMENSION(:)       :: AP,     AN,     AC,     ASI,    ACHLA,  APOM,   ANPR
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EG,     ER,     EE,     EM,     EB
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EHSN,   EHSP,   EHSSI,  ESAT,   EHS,    ENPR
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EP,     EN,     EC,     ESI,    ECHLA,  EPOM
  REAL,              ALLOCATABLE, DIMENSION(:)       :: BETA,   EXH2O,  EXSS,   EXOM,   EXA
  REAL,              ALLOCATABLE, DIMENSION(:)       :: DSIR,   PSIS,   PSIDK,  PARTSI
  REAL,              ALLOCATABLE, DIMENSION(:)       :: ORGP,   ORGN,   ORGC,   ORGSI
  REAL,              ALLOCATABLE, DIMENSION(:)       :: BODP,   BODN,   BODC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: PO4R,   PARTP
  REAL,              ALLOCATABLE, DIMENSION(:)       :: NH4DK,  NH4R,   NO3DK,  NO3S, FNO3SED
  REAL,              ALLOCATABLE, DIMENSION(:)       :: O2AG,   O2AR,   O2OM,   O2NH4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: O2EG,   O2ER
  REAL,              ALLOCATABLE, DIMENSION(:)       :: CO2R,   FER
  REAL,              ALLOCATABLE, DIMENSION(:)       :: KBOD,   TBOD,   RBOD
  REAL,              ALLOCATABLE, DIMENSION(:)       :: CAQ10,  CADK,   CAS
  REAL,              ALLOCATABLE, DIMENSION(:)       :: OMT1,   OMT2,   SODT1,  SODT2,  NH4T1,  NH4T2,  NO3T1,  NO3T2
  REAL,              ALLOCATABLE, DIMENSION(:)       :: OMK1,   OMK2,   SODK1,  SODK2,  NH4K1,  NH4K2,  NO3K1,  NO3K2
  REAL,              ALLOCATABLE, DIMENSION(:)       :: AT1,    AT2,    AT3,    AT4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: AK1,    AK2,    AK3,    AK4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: ET1,    ET2,    ET3,    ET4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: EK1,    EK2,    EK3,    EK4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: REAER,  WIND10, CZ,     QC,     QERR
  REAL,              ALLOCATABLE, DIMENSION(:)       :: RCOEF1, RCOEF2, RCOEF3, RCOEF4
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: DO1,    DO2,    DO3,    GAMMA
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: SED,    FPSS,   FPFE
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: CBODD
  REAL,              ALLOCATABLE, DIMENSION(:)       :: CBODS
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: ORGPLD,  ORGPRD,   ORGPLP,    ORGPRP,  ORGNLD,  ORGNRD, ORGNLP, ORGNRP
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: LDOMPMP, LDOMNMP,  LPOMPMP,   LPOMNMP, RPOMPMP, RPOMNMP
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: LPZOOINP,LPZOOINN, LPZOOOUTP, LPZOOOUTN
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: SEDC,    SEDN, SEDP
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: SEDVPC,  SEDVPP, SEDVPN
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: SDKV,    SEDDKTOT
  INTEGER                                            :: nldomp,nrdomp,nlpomp,nrpomp,nldomn,nrdomn,nlpomn,nrpomn
  INTEGER,           ALLOCATABLE, DIMENSION(:)       :: NAF,    NEQN,   ANEQN,  ENEQN
  INTEGER,           ALLOCATABLE, DIMENSION(:,:)     :: KFCN
  LOGICAL,           ALLOCATABLE, DIMENSION(:)       :: SEDIMENT_RESUSPENSION
  CHARACTER(8),      ALLOCATABLE, DIMENSION(:)       :: CAC,    REAERC
  CHARACTER(10),     ALLOCATABLE, DIMENSION(:,:)     :: LFPR
  CONTAINS
  Real    FUNCTION SATO (T,SAL,P,SALT_WATER)
      real(R8) T,SAL
      real P
      LOGICAL :: SALT_WATER
      SATO = EXP(7.7117-1.31403*(LOG(T+45.93)))*P
      IF (SALT_WATER) SATO = EXP(LOG(SATO)-SAL*(1.7674E-2-1.0754E1/(T+273.15)+2.1407E3/(T+273.15)**2))
    END FUNCTION SATO
  Real  FUNCTION FR (TT,TT1,TT2,SK1,SK2)
      real(r8)  tt
      real tt1,tt2,sk1,sk2
      FR = SK1*EXP(LOG(SK2*(1.0-SK1)/(SK1*(1.0-SK2)))/(TT2-TT1)*(TT-TT1))
    END FUNCTION FR
  Real  FUNCTION FF (TT,TT3,TT4,SK3,SK4)
      real tt3,tt4,sk3,sk4
      real(r8) tt
      FF = SK4*EXP(LOG(SK3*(1.0-SK4)/(SK4*(1.0-SK3)))/(TT4-TT3)*(TT4-TT))
    END FUNCTION FF
END MODULE KINETIC
MODULE SELWC
  REAL,              ALLOCATABLE, DIMENSION(:)   :: EWD,    VNORM,  QNEW, tavgw
  REAL,              ALLOCATABLE, DIMENSION(:,:) :: QSTR,   QSW,    ESTR,   WSTR, TAVG            ! SW Selective 7/30/09
  REAL,              ALLOCATABLE, DIMENSION(:,:) :: CAVGW, CDAVGW
  REAL,              ALLOCATABLE, DIMENSION(:,:,:):: CAVG, CDAVG
  INTEGER,           ALLOCATABLE, DIMENSION(:)   :: NSTR,   NOUT,   KTWD,   KBWD,   KTW,   KBW
  INTEGER,           ALLOCATABLE, DIMENSION(:,:) :: KTSW,   KBSW,   KOUT
END MODULE SELWC
MODULE GDAYC
  REAL                                           :: DAYM,   EQTNEW
  INTEGER                                        :: JDAYG,  M,      YEAR,   GDAY
  LOGICAL                                        :: LEAP_YEAR
  CHARACTER(9)                                   :: MONTH
END MODULE GDAYC
MODULE SCREENC
  USE PREC
  REAL                                           :: JDAY,   DLTS1,  JDMIN,  MINDLT, DLTAV,  ELTMJD
  REAL(R8),          ALLOCATABLE, DIMENSION(:)   :: ZMIN,   CMIN,   CMAX,   HYMIN,  HYMAX,  CDMIN,  CDMAX
  INTEGER                                        :: ILOC,   KLOC,   IMIN,   KMIN,   NIT,    NV,     JTT,     JWW
  INTEGER,           ALLOCATABLE, DIMENSION(:)   :: IZMIN
  CHARACTER(8),      ALLOCATABLE, DIMENSION(:)   :: ACPRC,  AHPRC,  ACDPRC
END MODULE SCREENC
MODULE TDGAS
  REAL,              ALLOCATABLE, DIMENSION(:)   :: AGASSP, BGASSP, CGASSP, AGASGT, BGASGT, CGASGT
  INTEGER,           ALLOCATABLE, DIMENSION(:)   :: EQSP,   EQGT
END MODULE TDGAS
MODULE LOGICC
  LOGICAL                                        :: SUSP_SOLIDS,        OXYGEN_DEMAND,    UPDATE_GRAPH,     INITIALIZE_GRAPH
  LOGICAL                                        :: WITHDRAWALS,        TRIBUTARIES,      GATES, PIPES
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: NO_WIND,            NO_INFLOW,        NO_OUTFLOW,       NO_HEAT
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: UPWIND,             ULTIMATE,         FRESH_WATER,      SALT_WATER
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: LIMITING_DLT,       TERM_BY_TERM,     MANNINGS_N,       PH_CALC
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: ONE_LAYER,          DIST_TRIBS,       PRECIPITATION
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: PRINT_SEDIMENT,     LIMITING_FACTOR,  READ_EXTINCTION,  READ_RADIATION
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: UH_INTERNAL,        DH_INTERNAL,      UH_EXTERNAL,      DH_EXTERNAL
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: UQ_INTERNAL,        DQ_INTERNAL,      UQ_EXTERNAL,      DQ_EXTERNAL
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: UP_FLOW,            DN_FLOW,          INTERNAL_FLOW
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: DAM_INFLOW,         DAM_OUTFLOW                                    !TC 08/03/04
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: INTERP_METEOROLOGY, INTERP_INFLOW,    INTERP_DTRIBS,    INTERP_TRIBS
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: INTERP_WITHDRAWAL,  INTERP_HEAD,      INTERP_EXTINCTION
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: VISCOSITY_LIMIT,    CELERITY_LIMIT,   IMPLICIT_AZ,      TRAPEZOIDAL !SW 07/16/04
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: HYDRO_PLOT,         CONSTITUENT_PLOT, DERIVED_PLOT
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: INTERP_gate     ! cb 8/13/2010
  LOGICAL,           ALLOCATABLE, DIMENSION(:,:) :: PRINT_DERIVED,      PRINT_HYDRO,      PRINT_CONST,      PRINT_EPIPHYTON
  LOGICAL,           ALLOCATABLE, DIMENSION(:,:) :: POINT_SINK,         INTERNAL_WEIR,    INTERP_OUTFLOW
END MODULE LOGICC
MODULE SHADEC
  integer, PARAMETER :: IANG=18
  REAL,PARAMETER                                 :: GAMA=(3.1415926*2.)/REAL(IANG)                         ! SW 10/17/05
  REAL,                           DIMENSION(IANG):: ANG                                                    ! SW 10/17/05
  REAL,              ALLOCATABLE, DIMENSION(:)   :: A00,    DECL,   HH,     TTLB,   TTRB,   CLLB,   CLRB   ! SW 10/17/05
  REAL,              ALLOCATABLE, DIMENSION(:)   :: SRLB1,  SRRB1,  SRLB2,  SRRB2,  SRFJD1, SRFJD2, SHADEI
  REAL,              ALLOCATABLE, DIMENSION(:,:) :: TOPO
  LOGICAL,           ALLOCATABLE, DIMENSION(:)   :: DYNAMIC_SHADE
  DATA ANG  /0.00000, 0.34907, 0.69813, 1.04720, 1.39626, 1.74533, 2.09440, 2.44346, &
            2.79253, 3.14159, 3.49066, 3.83972, 4.18879, 4.53786, 4.88692, 5.23599, 5.58505, 5.93412/      ! SW 10/17/05
END MODULE SHADEC
MODULE EDDY
USE PREC
  CHARACTER(8),      ALLOCATABLE, DIMENSION(:)      :: AZC,IMPTKE
  REAL,              ALLOCATABLE, DIMENSION(:)      :: WSHY,   FRIC
  REAL,              ALLOCATABLE, DIMENSION(:,:)    :: FRICBR, DECAY
  REAL(R8),          ALLOCATABLE, DIMENSION (:,:,:) :: TKE
  REAL(R8),          ALLOCATABLE, DIMENSION (:,:)   :: AZT, DZT
  REAL,              ALLOCATABLE, DIMENSION(:)      :: USTARBTKE, E
  REAL,              ALLOCATABLE, DIMENSION(:)      :: EROUGH, ARODI, TKELATPRDCONST, STRICK
  INTEGER,           ALLOCATABLE, DIMENSION(:)      :: FIRSTI, LASTI, WALLPNT, TKEBC
  LOGICAL,           ALLOCATABLE, DIMENSION(:)      :: STRICKON, TKELATPRD
END MODULE EDDY
MODULE MACROPHYTEC
  REAL,    POINTER,               DIMENSION(:,:)     :: NH4MR,  NH4MG,  LDOMMAC, RPOMMAC, LPOMMAC, DOMP, DOMR, TICMC
  REAL,    POINTER,               DIMENSION(:,:)     :: PO4MR,  PO4MG
  REAL,              ALLOCATABLE, DIMENSION(:)       :: MG,     MR,     MM, MMAX,   MBMP
  REAL,              ALLOCATABLE, DIMENSION(:)       :: MT1,    MT2,    MT3,    MT4,    MK1,    MK2,    MK3,    MK4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: MP,     MN,     MC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: PSED,   NSED,   MHSP,   MHSN,   MHSC,   msat,   exm
  REAL,              ALLOCATABLE, DIMENSION(:)       :: CDdrag, dwv,    dwsa,  anorm    ! cb 6/29/06
  REAL,              ALLOCATABLE, DIMENSION(:)       :: ARMAC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: O2MG,   O2MR,   LRPMAC,  MPOM
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: MACMBRS,MACMBRT,SSMACMB
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: CW,     BIC, macwbci
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: MACTRMR,MACTRMF,MACTRM
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: MMR,    MRR
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: MAC,    MACT
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: MPLIM,  MNLIM, MCLIM
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: SMAC,   SMACT
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: GAMMAJ
  REAL,              ALLOCATABLE, DIMENSION(:,:,:,:) :: MGR
  REAL,              ALLOCATABLE, DIMENSION(:,:,:,:) :: MACRC,  MACRM
  REAL,              ALLOCATABLE, DIMENSION(:,:,:,:) :: MLLIM
  REAL,              ALLOCATABLE, DIMENSION(:,:,:,:) :: MACSS
  REAL,              ALLOCATABLE, DIMENSION(:,:,:,:) :: SMACRC, SMACRM
  LOGICAL,           ALLOCATABLE, DIMENSION(:)       :: KTICOL
  LOGICAL,           ALLOCATABLE, DIMENSION(:,:)     :: PRINT_MACROPHYTE, MACROPHYTE_CALC
  LOGICAL                                            :: MACROPHYTE_ON
  CHARACTER(3),      ALLOCATABLE, DIMENSION(:,:)     :: mprwbc, macwbc
  CHARACTER(10),      ALLOCATABLE, DIMENSION(:,:)    :: CONV2
  CHARACTER(10),     ALLOCATABLE, DIMENSION(:,:,:,:) :: MLFPR
!  DATA                                                  SAVOLRAT /9000.0/, DEN /6.0E4/   !cb 6/30/06
END MODULE MACROPHYTEC
MODULE POROSITYC
    REAL,              ALLOCATABLE, DIMENSION(:)     :: SAREA, VOLKTI
    REAL,              ALLOCATABLE, DIMENSION(:,:)   :: POR,   VOLI,   VSTEMKT
    REAL,              ALLOCATABLE, DIMENSION(:,:,:) :: VSTEM
    LOGICAL,       ALLOCATABLE, DIMENSION(:)         :: HEAD_FLOW
    LOGICAL,       ALLOCATABLE, DIMENSION(:)         :: UP_HEAD
END MODULE POROSITYC
MODULE ZOOPLANKTONC
  USE PREC
  LOGICAL                                            :: ZOOPLANKTON_CALC
  REAL,              ALLOCATABLE, DIMENSION(:)       :: zg,zm,zeff,PREFP,zr,ZOOMIN,ZS2P,EXZ
  REAL,              ALLOCATABLE, DIMENSION(:)       :: Zt1,Zt2,Zt3,Zt4,Zk1,Zk2,Zk3,Zk4
  REAL,              ALLOCATABLE, DIMENSION(:)       :: ZP,ZN,ZC,o2zr
    REAL,              ALLOCATABLE, DIMENSION(:,:)   :: PREFA, PREFZ ! OMNIVOROUS ZOOPLANKTON
  REAL,              ALLOCATABLE, DIMENSION(:,:)     :: po4zr,NH4ZR,DOZR,TICZR,LPZOOOUT,LPZOOIN
  REAL(R8),POINTER,               DIMENSION(:,:,:)   :: ZOO, ZOOSS
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: ZMU,TGRAZE,ZRT,ZMT
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: ZOORM,ZOORMR,ZOORMF
  REAL,              ALLOCATABLE, DIMENSION(:,:,:)   :: agzt
  REAL,              ALLOCATABLE, DIMENSION(:,:,:,:) :: AGZ, ZGZ ! OMNIVOROUS ZOOPLANKTON
END MODULE ZOOPLANKTONC
module initialvelocity
  LOGICAL                                            :: init_vel, once_through
  REAL,          ALLOCATABLE, DIMENSION(:)           :: qssi,elwss,uavg
  REAL,          ALLOCATABLE, DIMENSION(:,:)         :: bsave
  LOGICAL,           ALLOCATABLE, DIMENSION(:)       :: loop_branch
end module initialvelocity
MODULE ENVIRPMOD
character*3, save, allocatable, dimension (:) :: CC_E,CD_E
character*3, save :: vel_vpr,temp_vpr
real, save :: temp_int,temp_top,vel_int,vel_top,sumvolt,timlast,dltt,v_cnt,v_sum,v_tot,volgl,t_crit,v_crit,c_crit,cd_crit,temp_c,v_avg,vel_c
real, save :: t_cnt,t_sum,t_tot,t_avg
real,allocatable, save, dimension (:) :: c_cnt,cd_cnt,c_tot,cd_tot,t_class,v_class,c_sum,cd_sum,c_avg,cd_avg
real,allocatable, save, dimension (:,:) :: c_class,cd_class,conc_c,conc_cd
real, allocatable, save, dimension (:) :: c_int,c_top,cd_int,cd_top,cn_e,cdn_e
integer CONE,numclass,iopenfish,nac_e,nacd_e,jj,jacd
values CONE/1500/
END MODULE ENVIRPMOD
Module MAIN
USE PREC
! Variable declaration
  INTEGER       :: J,NIW,NGC,NGCS,NTDS,NCCS,NGCE,NSSS,NSSE,NPO4,NNH4
  INTEGER       :: NNO3,NDSI,NPSI,NFE,NLDOM,NRDOM,NLPOM,NRPOM,NBODS
  INTEGER       :: NBODE, NAS, NAE, NDO, NTIC, NALK, NTRT, NWDT
  INTEGER       :: NDT, JS, JP, JG,JT, JH, NTSR, NIWDO, NRSO,JD
  INTEGER       :: nbodcs, nbodce, nbodps, nbodpe, nbodns, nbodne, ibod, jcb       ! cb 6/6/10
  INTEGER       :: JF,JA,JM,JE,JJZ,K,L3,L1,L2,NTAC,NDSP,NTACMX,NTACMN,JFILE
  INTEGER       :: KBP,JWR,JJJB,JDAYNX,NIT1,JWD,L,IUT,IDT,KTIP
  INTEGER       :: INCRIS,IE,II,NDLT,NRS,INCR,IS,JAC
  REAL          :: JDAYTS, JDAY1, TMSTRT, TMEND,HMAX, DLXMAX,CELRTY
  REAL(R8)      :: DLMR, TICE                        ! SW 4/19/10
  REAL          :: TAU1,TAU2, ELTMS, EPI,HMIN,DLXMIN, RHOICP
  REAL          :: NXTVD,TTIME, ZB,WWT,DFC,GC2,HRAD,EFFRIC,UDR,UDL,AB
  REAL          :: DEPKTI,COLB,COLDEP,SSTOT,RHOIN,VQIN,VQINI
  REAL          :: QINFR, ELT,RHOIRL1,V1,BHSUM,BHRSUM,WT1,WT2
!  REAL          :: ICETHU, ICETH1, ICETH2, ICE_TOL,TICE,DEL,HICE
  REAL          :: ICETHU, ICETH1, ICETH2, ICE_TOL,DEL,HICE            ! SW 4/19/10
  REAL          :: DLTCAL,HEATEX,SROOUT,SROSED,SROIN,SRONET,TFLUX,HIA
  REAL          :: TAIRV,EA,ES,DTV
  REAL          :: T2R4
!  INTEGER       :: CON,    RSI,    GRF,  NDG=16, ICPL
  INTEGER       :: CON,    RSI,    GRF,  NDG=16             ! cb 1/26/09
  integer       :: vsf,    sif 
  LOGICAL       :: ADD_LAYER,      SUB_LAYER,          WARNING_OPEN,    ERROR_OPEN,      VOLUME_WARNING, SURFACE_WARNING
  LOGICAL       :: END_RUN,        BRANCH_FOUND,       NEW_PAGE,        UPDATE_KINETICS, UPDATE_RATES
  LOGICAL       :: WEIR_CALC,      DERIVED_CALC,       RESTART_IN,      RESTART_OUT
  LOGICAL       :: SPILLWAY,       PUMPS
  LOGICAL       :: TIME_SERIES,    DOWNSTREAM_OUTFLOW, ICE_COMPUTATION            !, WINTER    ! SW/RC 4/28/11 eliminate WINTER
  CHARACTER(1)  :: ESC
  CHARACTER(2)  :: DEG
  CHARACTER(3)  :: GDCH
  CHARACTER(8)  :: RSOC,   RSIC,   CCC,   LIMC,   WDOC,   TSRC,   EXT, SELECTC, CLOSEC, HABTATC,ENVIRPC, AERATEC, INITUWL, DLTINTER      ! SW 7/31/09; 8/24/09
  CHARACTER(8)  ::SYSTDG,   N2BND,   DOBND  ! cb 112917
  CHARACTER(10) :: BLANK,  BLANK1, sedch,   sedpch,   sednch,   sedcch 
  CHARACTER(72) :: WDOFN,  RSOFN,  TSRFN, SEGNUM, LINE, SEGNUM2
  LOGICAL       :: RETLOG

! Allocatable array declarations

  REAL,          ALLOCATABLE, DIMENSION(:)     :: ETUGT,  EBUGT,  ETDGT,  EBDGT
  REAL,          ALLOCATABLE, DIMENSION(:)     :: ETUSP,  EBUSP,  ETDSP,  EBDSP
  REAL,          ALLOCATABLE, DIMENSION(:)     :: ETUPI,  EBUPI,  ETDPI,  EBDPI,  ETPU,   EBPU,   TSEDF
  REAL,          ALLOCATABLE, DIMENSION(:)     :: CSUM,   CDSUM,  X1
  REAL,          ALLOCATABLE, DIMENSION(:)     :: RSOD,   RSOF,   DLTD,   DLTF,   DLTMAX, QWDO
  REAL,          ALLOCATABLE, DIMENSION(:)     :: ICETHI, ALBEDO, HWI,    BETAI,  GAMMAI, ICEMIN, ICET2,  CBHE,   TSED
  REAL,          ALLOCATABLE, DIMENSION(:)     :: FI,     SEDCI,  FSOD,   FSED,   AX,     RANLW,    T2I,    ELBOT,  DXI
  REAL,          ALLOCATABLE, DIMENSION(:)     :: QINT,   QOUTT
  REAL,          ALLOCATABLE, DIMENSION(:)     :: WSHX,   SROSH,  EV
  REAL,          ALLOCATABLE, DIMENSION(:)     :: QDT,    QPR,    ICESW,  RS,     RN
  REAL,          ALLOCATABLE, DIMENSION(:)     :: XBR,    QPRBR,  EVBR,   TPB
  REAL(R8),      ALLOCATABLE, DIMENSION(:)     :: DLXRHO, Q,      QSSUM
  REAL,          ALLOCATABLE, DIMENSION(:)     :: ELTRT,  ELTRB
  REAL,          ALLOCATABLE, DIMENSION(:)     :: TSRD,   TSRF,   WDOD,   WDOF
  REAL,          ALLOCATABLE, DIMENSION(:)     :: QOAVR,  QIMXR,  QOMXR,  QTAVB,  QTMXB
  REAL,          ALLOCATABLE, DIMENSION(:)     :: FETCH,  ETSR
  REAL(R8),      ALLOCATABLE, DIMENSION(:)     :: QINSUM, TINSUM
  REAL,          ALLOCATABLE, DIMENSION(:)     :: CDTOT
  REAL,          ALLOCATABLE, DIMENSION(:)     :: SEDCIp, sedcin, sedcic, sedcis   !v3.5
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: ESTRT,  WSTRT,  CINSUM
  REAL(R8),      ALLOCATABLE, DIMENSION(:,:)   :: P,      HSEG,   QTOT
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: CPB,    COUT,   CWDO,   CDWDO, KFJW
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: C2I,    EPICI
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: QTRF
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: SNPD,   SCRD,   PRFD,   SPRD,   CPLD,   VPLD,   FLXD
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: SNPF,   SCRF,   PRFF,   SPRF,   CPLF,   VPLF,   FLXF
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: TVP,    SEDVP,  QINF
  REAL(R8),      ALLOCATABLE, DIMENSION(:,:)   :: TSSUH1, TSSDH1
  REAL(R8),      ALLOCATABLE, DIMENSION(:,:,:) :: CSSUH1, CSSDH1
  REAL,          ALLOCATABLE, DIMENSION(:,:,:) :: EPIVP,  CVP
  REAL(R8),      ALLOCATABLE, DIMENSION(:)     :: VOLB
  REAL(R8),      ALLOCATABLE, DIMENSION(:)     :: DLVOL,  VOLG
  REAL(R8),      ALLOCATABLE, DIMENSION(:)     :: A,      C,      D,      F,      V,      BTA,    GMA,    BHRHO
  REAL(R8),      ALLOCATABLE, DIMENSION(:)     :: DLVR,   ESR,    ETR
  REAL(R8),      ALLOCATABLE, DIMENSION(:,:)   :: CMBRS
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: KTUGT,  KBUGT,  KTDGT,  KBDGT
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: KTUSP,  KBUSP,  KTDSP,  KBDSP
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: KTUPI,  KBUPI,  KTDPI,  KBDPI
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: NSNP,   NSCR,   NSPR,   NVPL,   NFLX,   NCPL,   BTH
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: VPR,    LPR,    NIPRF,  NISPR,  NPRF
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: NISNP
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: NBL,    KBMAX,  KBI
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: KBR,    IBPR
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: TSR
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: NPOINT, NL,     KTQIN,  KBQIN, ilayer    ! SW 1/23/06
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: ITR,    KTTR,   KBTR,   JBTR
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: IWD,    KWD,    JBWD
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: IWDO,   ITSR
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: ILAT,   JBDAM,  JSS
  INTEGER,       ALLOCATABLE, DIMENSION(:)     :: icpl                                      ! cb 1/26/09
  INTEGER,       ALLOCATABLE, DIMENSION(:,:)   :: KTSWT,  KBSWT
  INTEGER,       ALLOCATABLE, DIMENSION(:,:)   :: IPRF,   ISPR,   ISNP,   BL,     WDO,    CDN, WDO2
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: ALLOW_ICE,           PUMPON,        FETCH_CALC,   ICE_IN  !     RC/SW 4/28/11
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: DN_HEAD,        HEAD_BOUNDARY
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: PLACE_QIN,      PLACE_QTR,      SPECIFY_QTR
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: OPEN_VPR,       OPEN_LPR
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: ISO_TEMP,       VERT_TEMP,      LONG_TEMP,     VERT_PROFILE,  LONG_PROFILE
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: SEDIMENT_CALC,  DETAILED_ICE,   IMPLICIT_VISC, SNAPSHOT,      PROFILE
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: VECTOR,         CONTOUR,        SPREADSHEET,   SCREEN_OUTPUT
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: FLUX,           EVAPORATION,    ZERO_SLOPE
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: ISO_SEDIMENT,   VERT_SEDIMENT,  LONG_SEDIMENT
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: VOLUME_BALANCE, ENERGY_BALANCE, MASS_BALANCE, BOD_CALC, ALG_CALC
  LOGICAL,       ALLOCATABLE, DIMENSION(:)     :: bod_calcp,bod_calcn                                                  ! cb 5/19/2011
  LOGICAL,       ALLOCATABLE, DIMENSION(:,:)   :: ISO_EPIPHYTON,  VERT_EPIPHYTON, LONG_EPIPHYTON, EPIPHYTON_CALC
  LOGICAL,       ALLOCATABLE, DIMENSION(:,:)   :: ISO_CONC,       VERT_CONC,      LONG_CONC,      TDG_SPILLWAY,   TDG_GATE
  CHARACTER(4),  ALLOCATABLE, DIMENSION(:)     :: CUNIT1
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: SEG,    SEDRC,  TECPLOT
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: HPLTC,  CPLTC,  CDPLTC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: EXC,    EXIC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: GASGTC, GASSPC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: CWDOC,  CDWDOC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: ICEC,   SEDCc,  SEDPRC, SNPC,   SCRC,   SPRC,   PRFC,DYNSEDK
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: RHEVC,  VPLC,   CPLC,   AZSLC,  FETCHC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: DTRC,   SROC,   KFAC,   CDAC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: INCAC,  TRCAC,  DTCAC,  PRCAC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: WTYPEC, GRIDC                                                        !SW 07/16/04
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: PUSPC,  PDSPC,  PUGTC,  PDGTC,  PDPIC,  PUPIC,  PPUC,   TRC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: SLICEC, FLXC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: VBC,    MBC,    EBC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: PQC,    EVC,    PRC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: QINC,   QOUTC,  WINDC,  HEATC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: VISC,   CELC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: SLTRC,  SLHTC,  FRICC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: QINIC,  TRIC,   DTRIC,  WDIC,   HDIC,   METIC, KFNAME2
  CHARACTER(10), ALLOCATABLE, DIMENSION(:)     :: C2CH,   CDCH,   EPCH,   macch, KFCH
  CHARACTER(45), ALLOCATABLE, DIMENSION(:)     :: KFNAME
  CHARACTER(72), ALLOCATABLE, DIMENSION(:)     :: SNPFN,  PRFFN,  VPLFN,  CPLFN,  SPRFN,  FLXFN,  FLXFN2, BTHFN,  VPRFN,  LPRFN
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:,:)   :: SINKC,  SINKCT
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:,:)   :: CPRBRC, CDTBRC, CPRWBC, CINBRC, CTRTRC, HPRWBC, STRIC,  CDWBC,  KFWBC
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:,:)   :: EPIC,   EPIPRC
  CHARACTER(10), ALLOCATABLE, DIMENSION(:,:)   :: CONV1
  CHARACTER(72), PARAMETER                     :: CONFN='w2_con.npt'
  CHARACTER(72)                                :: TEXT
  integer nproc

! values declarations
  Real  :: RK1, RL1, RIMT, RHOA, RHOI, VTOL, CP, thrkti
  DATA RK1   /2.12/,         RL1    /333507.0/, RIMT /0.0/, RHOA /1.25/, RHOI /916.0/, VTOL /1.0E3/, CP /4186.0/
  DATA ICE_TOL /0.005/
  DATA BLANK /'          '/, BLANK1 /'    -99.00'/
  DATA CON   /10/,  RSI /11/
  values thrkti /0.10/  !v3.5

END Module Main

Module Selective1 
 REAL                                          :: nxtstr, nxttcd, nxtsplit,tcdfreq,tfrqtmp
  CHARACTER(8)                                 :: tempc,tspltc
  CHARACTER(8), ALLOCATABLE, DIMENSION(:)      :: tcelevcon,tcyearly,tcntr,tspltcntr,monctr,tsyearly,DYNSEL, ELCONTSPL
  INTEGER                                      :: numtempc,numtsplt, tempn,nstt    
  INTEGER, ALLOCATABLE, DIMENSION(:)           :: tcnelev,tcjb,tcjs,tciseg,tspltjb,nouts,kstrsplt, jbmon, jsmon, ncountcw,SELD
  REAL,          ALLOCATABLE, DIMENSION(:,:)   :: tcelev, tempcrit,qstrfrac
  REAL,          ALLOCATABLE, DIMENSION(:)     :: tctemp,tctend,tctsrt,tcklay,tspltt,volm,qwdfrac,tstend,tstsrt,NXSEL,TEMP2
  INTEGER, ALLOCATABLE, DIMENSION(:,:)         :: jstsplt, ncountc
   REAL,          ALLOCATABLE, DIMENSION(:,:)  :: volmc 

End Module Selective1

module habitat
  integer :: ifish, n, nseg,iopenfish,kkmax,kseg,jjw
  real    :: voltot,O2CORR,DOSAT
  character*80, allocatable, dimension(:) :: fishname
  character*80 :: conhab,conavg,consurf,consod
  character*300 :: habline1, habline2,habline3,habline4,habline5,habline6,habline7
  real, allocatable, dimension(:) :: fishtempl,fishtemph,fishdo,habvol,phabvol,cdo,cpo4,cno3,cnh4,cchla,ctotp,cdos,cpo4s,cno3s,cnh4s,cchlas,ctotps,cgamma,ssedd
  integer, allocatable, dimension (:) :: isegvol
end module habitat

MODULE extra
  CHARACTER(1),  ALLOCATABLE, DIMENSION(:)     :: bthtype, vprtype
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: hyd_prin,cst_icon,cst_prin
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: cin_con, ctr_con, cdt_con, cpr_con
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: gen_name,alg_name, ss_name
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: zoo_name,epi_name, mac_name, bod_name
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: br_name,wb_name, tr_name
  CHARACTER(8),  ALLOCATABLE, DIMENSION(:)     :: pipe_name,spill_name, gate_name, pump_name  
  CHARACTER(72)                                :: CONFNwrite, selfnwrite, habfnwrite
  CHARACTER(72),  ALLOCATABLE, DIMENSION(:)     :: bthline1,bthline2,vprline1,vprline2
  CHARACTER(72),  ALLOCATABLE, DIMENSION(:)    :: bthfnwrite, vprfnwrite
  CHARACTER(43),     ALLOCATABLE, DIMENSION(:)       :: CNAME4, cdunit
  logical vertprf, bthchng,vprchng, w2select, w2hab
end module extra

! CE-QUAL-W2 computations
PROGRAM control_file_read_write
  USE MAIN
  USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC
  USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS
  USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  use ifport, only:chdir  
  INTEGER(4) length,istatus
  character*255 dirc  
  logical S_OPEN, S_EXIST,h_open, h_exist,changes_exist
  integer s_number,h_number, LL2, LL4
   
  w2select=.false.
  w2hab=.false.
  
call get_command_argument(1,dirc,length,istatus)  
dirc=TRIM(dirc)

if(length /= 0)then
    istatus=CHDIR(dirc)
    select case(istatus)
      case(2)  ! ENOENT
        write(*,*)'The directory does not exist:',dirc
      case(20)   ! ENOTDIR
        write(*,*)'This is not a directory:', dirc
      case(0)    ! no error
    end select  
endif

      vertprf=.true.
      bthchng=.true.
      vprchng=.true.
! reading input files
        call read_control_file40            !also reads bathymetry and vertical profile file(s)
        
! MISCELL card
        open(8027,file='MISCELL.csv',status='unknown')
        write(8027,151)
151     format('"SELECTC","HABTATC","ENVIRPC","AERATEC","INITUWL","SYSTDG","N2BND","DOBND"')        
        write (8027,152)SELECTC,HABTATC,ENVIRPC,AERATEC,INITUWL,SYSTDG,N2BND,DOBND
152    format('"',a8,'","',a8,'","',a8,'","',a8,'","',a8,'","',a8,'","',a8,'","',a8,'","')
        
!     w2l filename
      open(8013,file='w2l_filename.csv',status='unknown')
      if(vector(1))then        
        L1=LEN_TRIM(vplfn(1))
        write(8013,126)vplfn(1)(1:L1)
126     format('"',a,'"')
      else
        write(8013,126)'Not Used'
      end if
      
        
                
        INQUIRE (FILE='w2_selective.npt', OPENED=S_OPEN, EXIST=S_EXIST, NUMBER=S_NUMBER)  ! Check if file exists
        !IF(SELECTC == '      ON')CALL read_SELECTIVE
        IF(s_exist)CALL read_SELECTIVE
        INQUIRE (FILE='w2_habitat.npt', OPENED=h_OPEN, EXIST=h_EXIST, NUMBER=h_NUMBER)    ! Check if file exists
        !IF(HABTATC == '      ON')call read_fishhabitat
        IF(h_exist)call read_fishhabitat      
      CONSTITUENTS =  CCC  == '      ON'
      
      
      

      !  reading graph.npt
      allocate  (cname4(nct),cdunit(ndc))
      call read_graph_npt_file
      
      ! creating table of initial water surface elevations      
      call table_intial_water_surface_elevations
      
      ! creating table specifying initial temperatures, concentrations, and input files
      call table_intial_concentrations
      
      
! reading changes file
      INQUIRE (FILE='changes.csv', OPENED=S_OPEN, EXIST=changes_exist, NUMBER=S_NUMBER)  ! Check if changes.npt file exists
      if(changes_exist)then
        
        ! backing up control file and bathymetry files and vertical profile files
        CONFNwrite='w2_con_bak.npt'
        bthfnwrite=' '
        do jw=1,nwb
          LL2=  SCAN (bthfn(jw),'.')        
          LL4=  SCAN (bthfn(jw),' ')        
          bthfnwrite(jw)=bthfn(jw)(1:LL2-1)//'_bak.'//bthfn(jw)(LL2+1:LL4)
        end do
        vprfnwrite=' '
        do jw=1,nwb
          LL2=  SCAN (vprfn(jw),'.')        
          LL4=  SCAN (vprfn(jw),' ')        
          vprfnwrite(jw)=vprfn(jw)(1:LL2-1)//'_bak.'//vprfn(jw)(LL2+1:LL4)
        end do      
        call write_control_file40        !also backs up bathymetry file(s)
        IF(s_exist .and. SELECTC == '      ON')then
          selfnwrite='w2_selective_bak.npt'
          CALL write_SELECTIVE
        end if
        IF(h_exist .and. HABTATC == '      ON')then
          habfnwrite='w2_habitat_bak.npt'
          CALL write_fishhabitat
        end if
        
        call changes
        
        ! writing files with changes
       CONFNwrite='w2_con.npt'
       do jw=1,nwb        
        bthfnwrite(jw)=bthfn(jw)
        vprfnwrite(jw)=vprfn(jw)
       end do       
        call write_control_file40
        !IF(SELECTC == '      ON')then
        IF(w2select .and. SELECTC == '      ON')then
          selfnwrite='w2_selective.npt'
          CALL write_SELECTIVE
        end if
        !IF(HABTATC == '      ON')then
        IF(w2hab .and. HABTATC == '      ON')then
          habfnwrite='w2_habitat.npt'
          CALL write_fishhabitat
        end if
     end if
    
! creating table of inflows and outflows      
      call table_inflows_outflows
      
! creating table of water quality paramters
      call table_water_quality
      
! creating table of meteorological parameters
      call table_meteorology
      
!   creating table of output files    
      call table_output_files          
      

  STOP
  END


!**************************************************************
  subroutine read_control_file40
	USE MAIN
    USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use initialvelocity
  use extra
  !EXTERNAL RESTART_OUTPUT
    
  character*1 char1
  character*8 AID
  character*72 linetest
  CHARACTER(8)  :: IBLANK

  open(con,file=confn,status='old')

! Title and array dimensions

  ALLOCATE (TITLE(11))
  READ (CON,'(///(8X,A72))') (TITLE(J),J=1,10)
  READ (CON,'(//8X,5I8,2A8)') NWB, NBR, IMX, KMX, NPROC, CLOSEC                     ! SW 7/31/09
  READ (CON,'(//8X,8I8)')     NTR, NST, NIW, NWD, NGT, NSP, NPI, NPU
  READ (CON,'(//8X,7I8,a8)')  NGC, NSS, NAL, NEP, NBOD, nmc, nzp  
  READ (CON,'(//8X,I8,8A8)')  NOD,SELECTC,HABTATC,ENVIRPC,AERATEC,INITUWL,SYSTDG,N2BND,DOBND

  if(NPROC == 0)NPROC=1                                                                 ! SW 7/31/09
  !call omp_set_num_threads(NPROC)   ! set # of processors to NPROC  Moved to INPUT subroutine  TOGGLE FOR DEBUG
  if(SELECTC=='        ')then
     SELECTC='     OFF'
  endif
  

! Constituent numbers

  NTDS  = 1
  NGCS  = 2
  NGCE  = NGCS+NGC-1
  NSSS  = NGCE+1
  NSSE  = NSSS+NSS-1
  NPO4  = NSSE+1
  NNH4  = NPO4+1
  NNO3  = NNH4+1
  NDSI  = NNO3+1
  NPSI  = NDSI+1
  NFE   = NPSI+1
  NLDOM = NFE+1
  NRDOM = NLDOM+1
  NLPOM = NRDOM+1
  NRPOM = NLPOM+1
  NBODS = NRPOM+1
if(nbod.gt.0)then    ! variable stoichiometry for CBOD    ! cb 6/6/10
 ALLOCATE (nbodc(nbod), nbodp(nbod), nbodn(nbod))
  ibod=nbods
  nbodcs=ibod  
  do jcb=1,nbod
     nbodc(jcb)=ibod
     ibod=ibod+1    
  end do
  nbodce=ibod-1
  nbodps=ibod
  do jcb=1,nbod
    nbodp(jcb)=ibod
    ibod=ibod+1
  end do
  nbodpe=ibod-1
  nbodns=ibod
  do jcb=1,nbod
    nbodn(jcb)=ibod
    ibod=ibod+1
  end do
  nbodne=ibod-1
  ELSE
    NBODNS=1;NBODNE=1;NBODPS=1;NBODPE=1;NBODCS=1;NBODCE=1
end if
!    NBODE = NBODS+NBOD-1 
  NBODE = NBODS+NBOD*3-1
  NAS   = NBODE+1
  NAE   = NAS+NAL-1
  NDO   = NAE+1
  NTIC  = NDO+1
  NALK  = NTIC+1

! v3.5 start
  NZOOS = NALK + 1
  NZOOE = NZOOS + NZP - 1
  NLDOMP=NZOOE+1
  NRDOMP=nldomp+1
  NLPOMP=nrdomp+1
  NRPOMP=nlpomp+1
  NLDOMn=nrpomp+1
  NRDOMn=nldomn+1
  NLPOMn=nrdomn+1
  NRPOMn=nlpomn+1
  nct=nrpomn
! v3.5 end

! Constituent, tributary, and widthdrawal totals

  NTRT = NTR+NGT+NSP+NPI+NPU
  NWDT = NWD+NGT+NSP+NPI+NPU
  NEPT = MAX(NEP,1)
  Nmct = MAX(nmc,1)
  nzpt=max(nzp,1)
  ALLOCATE (CDAC(NDC), X1(IMX), TECPLOT(NWB))
  ALLOCATE (WSC(IMX),    KBI(IMX))
  ALLOCATE (VBC(NWB),    EBC(NWB),    MBC(NWB),    PQC(NWB),    EVC(NWB),    PRC(NWB))
  ALLOCATE (WINDC(NWB),  QINC(NWB),   QOUTC(NWB),  HEATC(NWB),  SLHTC(NWB))
  ALLOCATE (QINIC(NBR),  DTRIC(NBR),  TRIC(NTR),   WDIC(NWD),   HDIC(NBR),   METIC(NWB))
  ALLOCATE (EXC(NWB),    EXIC(NWB))
  ALLOCATE (SLTRC(NWB),  THETA(NWB),  FRICC(NWB),  NAF(NWB),    ELTMF(NWB), Z0(NWB))
  ALLOCATE (ZMIN(NWB),   IZMIN(NWB))
  ALLOCATE (C2CH(NCT),   CDCH(NDC),   EPCH(NEPT),  macch(nmct), KFCH(NFL))  !v3.5
  ALLOCATE (CPLTC(NCT),  HPLTC(NHY),  CDPLTC(NDC))
  ALLOCATE (CMIN(NCT),   CMAX(NCT),   HYMIN(NHY),  HYMAX(NHY),  CDMIN(NDC),  CDMAX(NDC))
  ALLOCATE (JBDAM(NBR),  ILAT(NWDT))
  ALLOCATE (QINSUM(NBR), TINSUM(NBR), TIND(NBR),   JSS(NBR),    QIND(NBR))
  ALLOCATE (QOLD(NPI),   DTP(NPI),    DTPS(NPI),   QOLDS(NPI))
  ALLOCATE (LATGTC(NGT), LATSPC(NSP), LATPIC(NPI), DYNPIPE(NPI),DYNPUMP(NPU),LATPUC(NPU), DYNGTC(NGT))
  ALLOCATE (gtic(NGT),BGTo(NGT),   EGTo(NGT) )   ! cb 8/13/2010
  ALLOCATE (INTERP_gate(ngt))                     ! cb 8/13/2010  
  ALLOCATE (OPT(NWB,7),         CIND(NCT,NBR),         CINSUM(NCT,NBR))
  ALLOCATE (CDWBC(NDC,NWB),     KFWBC(NFL,NWB),        CPRWBC(NCT,NWB),    CINBRC(NCT,NBR),     CTRTRC(NCT,NTR))
  ALLOCATE (CDTBRC(NCT,NBR),    CPRBRC(NCT,NBR))
  ALLOCATE (YSS(NNPIPE,NPI),    VSS(NNPIPE,NPI),       YS(NNPIPE,NPI),     VS(NNPIPE,NPI),      VSTS(NNPIPE,NPI))
  ALLOCATE (YSTS(NNPIPE,NPI),   YST(NNPIPE,NPI),       VST(NNPIPE,NPI))
  ALLOCATE (CBODD(KMX,IMX,NBOD))
  ALLOCATE (ALLIM(KMX,IMX,NAL), APLIM(KMX,IMX,NAL),    ANLIM(KMX,IMX,NAL), ASLIM(KMX,IMX,NAL))
  ALLOCATE (ELLIM(KMX,IMX,NEP), EPLIM(KMX,IMX,NEP),    ENLIM(KMX,IMX,NEP), ESLIM(KMX,IMX,NEP))
  ALLOCATE (CSSK(KMX,IMX,NCT),  C1(KMX,IMX,NCT),       C2(KMX,IMX,NCT),    CD(KMX,IMX,NDC),     KF(KMX,IMX,NFL))
  ALLOCATE (KFS(KMX,IMX,NFL),   AF(KMX,IMX,NAL,5),     EF(KMX,IMX,NEP,5),  HYD(KMX,IMX,NHY), KFJW(NWB,NFL))
  ALLOCATE (TKE(KMX,IMX,3), AZT(KMX,IMX),DZT(KMX,IMX))
  ALLOCATE (USTARBTKE(IMX),E(IMX),EROUGH(NWB), ARODI(NWB), STRICK(NWB), TKELATPRDCONST(NWB))
  ALLOCATE (FIRSTI(NWB), LASTI(NWB), TKELATPRD(NWB), STRICKON(NWB), WALLPNT(NWB), IMPTKE(NWB), TKEBC(NWB))
  ALLOCATE (HYDRO_PLOT(NHY),    CONSTITUENT_PLOT(NCT), DERIVED_PLOT(NDC))
  ALLOCATE (ZERO_SLOPE(NWB),    DYNAMIC_SHADE(IMX))
  ALLOCATE (AZSLC(NWB))
  ALLOCATE (NSPRF(NWB))
  ALLOCATE (KBMAX(NWB),  ELKT(NWB),   WIND2(IMX))
  ALLOCATE (VISC(NWB),   CELC(NWB),   REAERC(NWB))
  ALLOCATE (QOAVR(NWB),  QIMXR(NWB),  QOMXR(NWB))
  ALLOCATE (LAT(NWB),    LONGIT(NWB), ELBOT(NWB))
  ALLOCATE (BTH(NWB),    VPR(NWB),    LPR(NWB))
  ALLOCATE (NISNP(NWB),  NIPRF(NWB),  NISPR(NWB))
  ALLOCATE (icpl(NWB))                                 ! cb 1/26/09
  ALLOCATE (A00(NWB),    HH(NWB),     DECL(NWB))
  ALLOCATE (T2I(NWB),    KTWB(NWB),   KBR(NWB),    IBPR(NWB))
  ALLOCATE (DLVR(NWB),   ESR(NWB),    ETR(NWB),    NBL(NWB))
  ALLOCATE (LPRFN(NWB),  EXTFN(NWB),  BTHFN(NWB),  METFN(NWB),  VPRFN(NWB))
  ALLOCATE (SNPFN(NWB),  PRFFN(NWB),  SPRFN(NWB),  CPLFN(NWB),  VPLFN(NWB),  FLXFN(NWB),FLXFN2(NWB))
  ALLOCATE (AFW(NWB),    BFW(NWB),    CFW(NWB),    WINDH(NWB),  RHEVC(NWB),  FETCHC(NWB))
  ALLOCATE (SDK(NWB),    FSOD(NWB),   FSED(NWB),   SEDCI(NWB),  SEDCc(NWB),   SEDPRC(NWB), seds(nwb), sedb(nwb), DYNSEDK(NWB))  !cb 11/28/06
  ALLOCATE (ICEC(NWB),   SLICEC(NWB), ICETHI(NWB), ALBEDO(NWB), HWI(NWB),    BETAI(NWB),  GAMMAI(NWB), ICEMIN(NWB), ICET2(NWB))
  ALLOCATE (EXH2O(NWB),  BETA(NWB),   EXOM(NWB),   EXSS(NWB),   DXI(NWB),    CBHE(NWB),   TSED(NWB),   TSEDF(NWB),  FI(NWB))
  ALLOCATE (AX(NWB),     WTYPEC(NWB), JBDN(NWB),   AZC(NWB),    AZMAX(NWB),  QINT(NWB),   QOUTT(NWB),  GRIDC(NWB))     !SW 07/14/04
  ALLOCATE (TAIR(NWB),   TDEW(NWB),   WIND(NWB),   PHI(NWB),    CLOUD(NWB),  CSHE(IMX),   SRON(NWB),   RANLW(NWB))
  ALLOCATE (SNPC(NWB),   SCRC(NWB),   PRFC(NWB),   SPRC(NWB),   CPLC(NWB),   VPLC(NWB),   FLXC(NWB))
  ALLOCATE (NXTMSN(NWB), NXTMSC(NWB), NXTMPR(NWB), NXTMSP(NWB), NXTMCP(NWB), NXTMVP(NWB), NXTMFL(NWB))
  ALLOCATE (SNPDP(NWB),  SCRDP(NWB),  PRFDP(NWB),  SPRDP(NWB),  CPLDP(NWB),  VPLDP(NWB),  FLXDP(NWB))
  ALLOCATE (NSNP(NWB),   NSCR(NWB),   NPRF(NWB),   NSPR(NWB),   NCPL(NWB),   NVPL(NWB),   NFLX(NWB))
  ALLOCATE (NEQN(NWB),   PO4R(NWB),   PARTP(NWB))
  ALLOCATE (NH4DK(NWB),  NH4R(NWB))
  ALLOCATE (NO3DK(NWB),  NO3S(NWB), FNO3SED(NWB))
  ALLOCATE (FER(NWB),    FES(NWB))
  ALLOCATE (CO2R(NWB),   SROC(NWB))
  ALLOCATE (O2ER(NEPT),  O2EG(NEPT))
  ALLOCATE (CAQ10(NWB),  CADK(NWB),   CAS(NWB))
  ALLOCATE (BODP(NBOD),  BODN(NBOD),  BODC(NBOD))
  ALLOCATE (KBOD(NBOD),  TBOD(NBOD),  RBOD(NBOD))
  ALLOCATE (LDOMDK(NWB), RDOMDK(NWB), LRDDK(NWB))
  ALLOCATE (OMT1(NWB),   OMT2(NWB),   OMK1(NWB),   OMK2(NWB))
  ALLOCATE (LPOMDK(NWB), RPOMDK(NWB), LRPDK(NWB),  POMS(NWB))
  ALLOCATE (ORGP(NWB),   ORGN(NWB),   ORGC(NWB),   ORGSI(NWB))
  ALLOCATE (RCOEF1(NWB), RCOEF2(NWB), RCOEF3(NWB), RCOEF4(NWB))
  ALLOCATE (NH4T1(NWB),  NH4T2(NWB),  NH4K1(NWB),  NH4K2(NWB))
  ALLOCATE (NO3T1(NWB),  NO3T2(NWB),  NO3K1(NWB),  NO3K2(NWB))
  ALLOCATE (DSIR(NWB),   PSIS(NWB),   PSIDK(NWB),  PARTSI(NWB))
  ALLOCATE (SODT1(NWB),  SODT2(NWB),  SODK1(NWB),  SODK2(NWB))
  ALLOCATE (O2NH4(NWB),  O2OM(NWB))
  ALLOCATE (O2AR(NAL),   O2AG(NAL))
  ALLOCATE (CGQ10(NGC),  CG0DK(NGC),  CG1DK(NGC),  CGS(NGC), CGLDK(NGC),CGKLF(NGC),CGCS(NGC))
  ALLOCATE (CUNIT(NCT),  CUNIT1(NCT), CUNIT2(NCT))
  ALLOCATE (CAC(NCT),    INCAC(NCT),  TRCAC(NCT),  DTCAC(NCT),  PRCAC(NCT))
  ALLOCATE (CNAME(NCT),  CNAME1(NCT), CNAME2(NCT), CNAME3(NCT), CMULT(NCT),  CSUM(NCT))
  ALLOCATE (CN(NCT))
  ALLOCATE (SSS(NSS),    TAUCR(NSS),  SEDRC(NSS))
  ALLOCATE (CDSUM(NDC))
  ALLOCATE (DTRC(NBR))
  ALLOCATE (NSTR(NBR),   XBR(NBR))
  ALLOCATE (QTAVB(NBR),  QTMXB(NBR))
  ALLOCATE (BS(NWB),     BE(NWB),     JBUH(NBR),   JBDH(NBR),   JWUH(NBR),   JWDH(NBR))
  ALLOCATE (TSSS(NBR),   TSSB(NBR),   TSSICE(NBR))
  ALLOCATE (ESBR(NBR),   ETBR(NBR),   EBRI(NBR))
  ALLOCATE (QIN(NBR),    PR(NBR),     QPRBR(NBR),  QDTR(NBR),   EVBR(NBR))
  ALLOCATE (TIN(NBR),    TOUT(NBR),   TPR(NBR),    TDTR(NBR),   TPB(NBR))
  ALLOCATE (NACPR(NBR),  NACIN(NBR),  NACDT(NBR),  NACTR(NTR),  NACD(NWB))
  ALLOCATE (QSUM(NBR),   NOUT(NBR),   KTQIN(NBR),  KBQIN(NBR),  ELUH(NBR),   ELDH(NBR))
  ALLOCATE (NL(NBR),     NPOINT(NBR), SLOPE(NBR),  SLOPEC(NBR), ALPHA(NBR),  COSA(NBR),   SINA(NBR),   SINAC(NBR), ilayer(imx))   
  ALLOCATE (CPRFN(NBR),  EUHFN(NBR),  TUHFN(NBR),  CUHFN(NBR),  EDHFN(NBR),  TDHFN(NBR),  QOTFN(NBR),  PREFN(NBR))
  ALLOCATE (QINFN(NBR),  TINFN(NBR),  CINFN(NBR),  CDHFN(NBR),  QDTFN(NBR),  TDTFN(NBR),  CDTFN(NBR),  TPRFN(NBR))
  ALLOCATE (VOLWD(NBR),  VOLSBR(NBR), VOLTBR(NBR), DLVOL(NBR),  VOLG(NWB),   VOLSR(NWB),  VOLTR(NWB),  VOLEV(NBR))
  ALLOCATE (VOLB(NBR),   VOLPR(NBR),  VOLTRB(NBR), VOLDT(NBR),  VOLUH(NBR),  VOLDH(NBR),  VOLIN(NBR),  VOLOUT(NBR))
  ALLOCATE (US(NBR),     DS(NBR),     CUS(NBR),    UHS(NBR),    DHS(NBR),    UQB(NBR),    DQB(NBR),    CDHS(NBR))
  ALLOCATE (TSSEV(NBR),  TSSPR(NBR),  TSSTR(NBR),  TSSDT(NBR),  TSSWD(NBR),  TSSUH(NBR),  TSSDH(NBR),  TSSIN(NBR),  TSSOUT(NBR))
  ALLOCATE (ET(IMX),     RS(IMX),     RN(IMX),     RB(IMX),     RC(IMX),     RE(IMX),     SHADE(IMX))
  ALLOCATE (DLTMAX(NOD), QWDO(IMX),   TWDO(IMX))                                                                        ! SW 1/24/05
  ALLOCATE (SOD(IMX),    ELWS(IMX),   BKT(IMX),    REAER(IMX))
  ALLOCATE (ICETH(IMX),  ICE(IMX),    ICESW(IMX))
  ALLOCATE (Q(IMX),      QC(IMX),     QERR(IMX),   QSSUM(IMX))
  ALLOCATE (KTI(IMX),    SKTI(IMX),   SROSH(IMX),  SEG(IMX),    DLXRHO(IMX))
  ALLOCATE (DLX(IMX),    DLXR(IMX))
  ALLOCATE (A(IMX),      C(IMX),      D(IMX),      F(IMX),      V(IMX),      BTA(IMX),    GMA(IMX))
  ALLOCATE (KBMIN(IMX),  EV(IMX),     QDT(IMX),    QPR(IMX),    SBKT(IMX),   BHRHO(IMX))
  ALLOCATE (SZ(IMX),     WSHX(IMX),   WSHY(IMX),   WIND10(IMX), CZ(IMX),     FETCH(IMX),  PHI0(IMX),   FRIC(IMX))
  ALLOCATE (Z(IMX),      KB(IMX),     PALT(IMX))
  ALLOCATE (VNORM(KMX))
  ALLOCATE (ANPR(NAL),   ANEQN(NAL),  APOM(NAL))
  ALLOCATE (AC(NAL),     ASI(NAL),    ACHLA(NAL),  AHSP(NAL),   AHSN(NAL),   AHSSI(NAL))
  ALLOCATE (AT1(NAL),    AT2(NAL),    AT3(NAL),    AT4(NAL),    AK1(NAL),    AK2(NAL),    AK3(NAL),    AK4(NAL))
  ALLOCATE (AG(NAL),     AR(NAL),     AE(NAL),     AM(NAL),     AS(NAL),     EXA(NAL),    ASAT(NAL),   AP(NAL),   AN(NAL))
  ALLOCATE (ENPR(NEPT),  ENEQN(NEPT))
  ALLOCATE (EG(NEPT),    ER(NEPT),    EE(NEPT),    EM(NEPT),    EB(NEPT),    ESAT(NEPT),  EP(NEPT),    EN(NEPT))
  ALLOCATE (EC(NEPT),    ESI(NEPT),   ECHLA(NEPT), EHSP(NEPT),  EHSN(NEPT),  EHSSI(NEPT), EPOM(NEPT),  EHS(NEPT))
  ALLOCATE (ET1(NEPT),   ET2(NEPT),   ET3(NEPT),   ET4(NEPT),   EK1(NEPT),   EK2(NEPT),   EK3(NEPT),   EK4(NEPT))
  ALLOCATE (HNAME(NHY),  FMTH(NHY),   HMULT(NHY),  FMTC(NCT),   FMTCD(NDC))
  ALLOCATE (KFAC(NFL),   KFNAME(NFL), KFNAME2(NFL),KFCN(NFL,NWB))
  ALLOCATE (C2I(NCT,NWB),    TRCN(NCT,NTR))
  ALLOCATE (CDN(NDC,NWB),    CDNAME(NDC),     CDNAME2(NDC),    CDNAME3(NDC),    CDMULT(NDC))
  ALLOCATE (CMBRS(NCT,NBR),  CMBRT(NCT,NBR),  INCN(NCT,NBR),   DTCN(NCT,NBR),   PRCN(NCT,NBR))
  ALLOCATE (FETCHU(IMX,NBR), FETCHD(IMX,NBR))
  ALLOCATE (IPRF(IMX,NWB),   ISNP(IMX,NWB),   ISPR(IMX,NWB),   BL(IMX,NWB))
  ALLOCATE (H1(KMX,IMX),     H2(KMX,IMX),     BH1(KMX,IMX),    BH2(KMX,IMX),    BHR1(KMX,IMX),   BHR2(KMX,IMX),   QTOT(KMX,IMX))
  ALLOCATE (SAVH2(KMX,IMX),  AVH1(KMX,IMX),   AVH2(KMX,IMX),   AVHR(KMX,IMX),   SAVHR(KMX,IMX))
  ALLOCATE (LFPR(KMX,IMX),   BI(KMX,IMX), bnew(kmx,imx))        ! SW 1/23/06
  ALLOCATE (ADX(KMX,IMX),    ADZ(KMX,IMX),    DO1(KMX,IMX),    DO2(KMX,IMX),    DO3(KMX,IMX),    SED(KMX,IMX))
  ALLOCATE (B(KMX,IMX),      CONV(KMX,IMX),   CONV1(KMX,IMX),  EL(KMX,IMX),     DZ(KMX,IMX),     DZQ(KMX,IMX),    DX(KMX,IMX))
  ALLOCATE (P(KMX,IMX),      SU(KMX,IMX),     SW(KMX,IMX),     SAZ(KMX,IMX),    T1(KMX,IMX),     TSS(KMX,IMX),    QSS(KMX,IMX))
  ALLOCATE (BB(KMX,IMX),     BR(KMX,IMX),     BH(KMX,IMX),     BHR(KMX,IMX),    VOL(KMX,IMX),    HSEG(KMX,IMX),   DECAY(KMX,IMX))
  ALLOCATE (DEPTHB(KMX,IMX), DEPTHM(KMX,IMX), FPSS(KMX,IMX),   FPFE(KMX,IMX),   FRICBR(KMX,IMX), UXBR(KMX,IMX),   UYBR(KMX,IMX))
  ALLOCATE (QUH1(KMX,NBR),   QDH1(KMX,NBR),   VOLUH2(KMX,NBR), VOLDH2(KMX,NBR), TUH(KMX,NBR),    TDH(KMX,NBR))
  ALLOCATE (TSSUH1(KMX,NBR), TSSUH2(KMX,NBR), TSSDH1(KMX,NBR), TSSDH2(KMX,NBR))
  ALLOCATE (TVP(KMX,NWB),    SEDVP(KMX,NWB),  H(KMX,NWB))
  ALLOCATE (QINF(KMX,NBR),   QOUT(KMX,NBR),   KOUT(KMX,NBR))
  ALLOCATE (CT(KMX,IMX),     AT(KMX,IMX),     VT(KMX,IMX),     DT(KMX,IMX),     GAMMA(KMX,IMX))
  ALLOCATE (CWDO(NCT,NOD),   CDWDO(NDC,NOD),  CWDOC(NCT),      CDWDOC(NDC),     CDTOT(NDC))
  ALLOCATE (CIN(NCT,NBR),    CDTR(NCT,NBR),   CPR(NCT,NBR),    CPB(NCT,NBR),    COUT(NCT,NBR))
  ALLOCATE (RSOD(NOD),       RSOF(NOD),       DLTD(NOD),       DLTF(NOD))
  ALLOCATE (TSRD(NOD),       TSRF(NOD),       WDOD(NOD),       WDOF(NOD))
  ALLOCATE (SNPD(NOD,NWB),   SNPF(NOD,NWB),   SPRD(NOD,NWB),   SPRF(NOD,NWB))
  ALLOCATE (SCRD(NOD,NWB),   SCRF(NOD,NWB),   PRFD(NOD,NWB),   PRFF(NOD,NWB))
  ALLOCATE (CPLD(NOD,NWB),   CPLF(NOD,NWB),   VPLD(NOD,NWB),   VPLF(NOD,NWB),   FLXD(NOD,NWB),   FLXF(NOD,NWB))
  ALLOCATE (EPIC(NWB,NEPT),  EPICI(NWB,NEPT), EPIPRC(NWB,NEPT))
  ALLOCATE (EPIVP(KMX,NWB,NEP))
  ALLOCATE (CUH(KMX,NCT,NBR),     CDH(KMX,NCT,NBR))
  ALLOCATE (EPM(KMX,IMX,NEPT),    EPD(KMX,IMX,NEPT),    EPC(KMX,IMX,NEPT))
  ALLOCATE (C1S(KMX,IMX,NCT),     CSSB(KMX,IMX,NCT),    CVP(KMX,NCT,NWB))
  ALLOCATE (CSSUH1(KMX,NCT,NBR),  CSSUH2(KMX,NCT,NBR),  CSSDH2(KMX,NCT,NBR), CSSDH1(KMX,NCT,NBR))
  ALLOCATE (OPEN_VPR(NWB),        OPEN_LPR(NWB))
  ALLOCATE (READ_EXTINCTION(NWB), READ_RADIATION(NWB))
  ALLOCATE (DIST_TRIBS(NBR),      LIMITING_FACTOR(NAL))
  ALLOCATE (UPWIND(NWB),          ULTIMATE(NWB))
  ALLOCATE (STRIC(NST,NBR), ESTRT(NST,NBR), WSTRT(NST,NBR), KTSWT(NST,NBR), KBSWT(NST,NBR),SINKCT(NST,NBR))
  allocate (estr(nst,nbr))
  ALLOCATE (FRESH_WATER(NWB),     SALT_WATER(NWB),      TRAPEZOIDAL(NWB))                                              !SW 07/16/04
  ALLOCATE (UH_EXTERNAL(NBR),     DH_EXTERNAL(NBR),     UH_INTERNAL(NBR),    DH_INTERNAL(NBR))
  ALLOCATE (UQ_EXTERNAL(NBR),     DQ_EXTERNAL(NBR),     UQ_INTERNAL(NBR),    DQ_INTERNAL(NBR))
  ALLOCATE (UP_FLOW(NBR),         DN_FLOW(NBR),         UP_HEAD(NBR),        DN_HEAD(NBR))
  ALLOCATE (INTERNAL_FLOW(NBR),   DAM_INFLOW(NBR),      DAM_OUTFLOW(NBR),    HEAD_FLOW(NBR),      HEAD_BOUNDARY(NWB))  !TC 08/03/04
  ALLOCATE (ISO_CONC(NCT,NWB),    VERT_CONC(NCT,NWB),   LONG_CONC(NCT,NWB))
  ALLOCATE (ISO_SEDIMENT(NWB),    VERT_SEDIMENT(NWB),   LONG_SEDIMENT(NWB))
  ALLOCATE (VISCOSITY_LIMIT(NWB), CELERITY_LIMIT(NWB),  IMPLICIT_AZ(NWB))
  ALLOCATE (FETCH_CALC(NWB),      ONE_LAYER(IMX),       IMPLICIT_VISC(NWB))
  ALLOCATE (LIMITING_DLT(NWB),    TERM_BY_TERM(NWB),    MANNINGS_N(NWB))
  ALLOCATE (PLACE_QIN(NWB),       PLACE_QTR(NTRT),      SPECIFY_QTR(NTRT))
  ALLOCATE (PRINT_CONST(NCT,NWB), PRINT_HYDRO(NHY,NWB), PRINT_SEDIMENT(NWB))
  ALLOCATE (VOLUME_BALANCE(NWB),  ENERGY_BALANCE(NWB),  MASS_BALANCE(NWB))
  ALLOCATE (DETAILED_ICE(NWB),    ICE_CALC(NWB),               ALLOW_ICE(IMX))           !   ICE_IN(NBR),    RC/SW 4/28/11
  ALLOCATE (EVAPORATION(NWB),     PRECIPITATION(NWB),   RH_EVAP(NWB),         PH_CALC(NWB))
  ALLOCATE (NO_INFLOW(NWB),       NO_OUTFLOW(NWB),      NO_HEAT(NWB),         NO_WIND(NWB))
  ALLOCATE (ISO_TEMP(NWB),        VERT_TEMP(NWB),       LONG_TEMP(NWB),       VERT_PROFILE(NWB),  LONG_PROFILE(NWB))
  ALLOCATE (SNAPSHOT(NWB),        PROFILE(NWB),         VECTOR(NWB),          CONTOUR(NWB),       SPREADSHEET(NWB))
  ALLOCATE (SCREEN_OUTPUT(NWB),   FLUX(NWB))
  ALLOCATE (PRINT_DERIVED(NDC,NWB),  PRINT_EPIPHYTON(NWB,NEPT))
  ALLOCATE (SEDIMENT_CALC(NWB),      EPIPHYTON_CALC(NWB,NEPT), SEDIMENT_RESUSPENSION(NSS),BOD_CALC(NBOD),ALG_CALC(NAL))
  ALLOCATE (BOD_CALCp(NBOD), BOD_CALCn(NBOD))                                                ! cb 5/19/2011
  ALLOCATE (TDG_SPILLWAY(NWDT,NSP),  TDG_GATE(NWDT,NGT),       INTERNAL_WEIR(KMX,IMX))
  ALLOCATE (ISO_EPIPHYTON(NWB,NEPT), VERT_EPIPHYTON(NWB,NEPT), LONG_EPIPHYTON(NWB,NEPT))
  ALLOCATE (LATERAL_SPILLWAY(NSP),   LATERAL_GATE(NGT),        LATERAL_PUMP(NPU),        LATERAL_PIPE(NPI))
  ALLOCATE (INTERP_HEAD(NBR),        INTERP_WITHDRAWAL(NWD),   INTERP_EXTINCTION(NWB),   INTERP_DTRIBS(NBR))
  ALLOCATE (INTERP_OUTFLOW(NST,NBR), INTERP_INFLOW(NBR),       INTERP_METEOROLOGY(NWB),  INTERP_TRIBS(NTR))
  ALLOCATE (LNAME(NCT+NHY+NDC))
  ALLOCATE (IWR(NIW),    KTWR(NIW),   KBWR(NIW))
  ALLOCATE (JWUSP(NSP),  JWDSP(NSP),  QSP(NSP))
  ALLOCATE (KTWD(NWDT),  KBWD(NWDT),  JBWD(NWDT))
  ALLOCATE (GTA1(NGT),   GTB1(NGT),   GTA2(NGT),   GTB2(NGT))
  ALLOCATE (BGT(NGT),    IUGT(NGT),   IDGT(NGT),   EGT(NGT),    EGT2(NGT))
  ALLOCATE (QTR(NTRT),   TTR(NTRT),   KTTR(NTRT),  KBTR(NTRT))
  ALLOCATE (AGASGT(NGT), BGASGT(NGT), CGASGT(NGT), GASGTC(NGT))
  ALLOCATE (PUGTC(NGT),  ETUGT(NGT),  EBUGT(NGT),  KTUGT(NGT),  KBUGT(NGT))
  ALLOCATE (PDGTC(NGT),  ETDGT(NGT),  EBDGT(NGT),  KTDGT(NGT),  KBDGT(NGT))
  ALLOCATE (A1GT(NGT),   B1GT(NGT),   G1GT(NGT),   A2GT(NGT),   B2GT(NGT),   G2GT(NGT))
  ALLOCATE (EQGT(NGT),   JBUGT(NGT),  JBDGT(NGT),  JWUGT(NGT),  JWDGT(NGT),  QGT(NGT))
  ALLOCATE (JBUPI(NPI),  JBDPI(NPI),  JWUPI(NPI),  JWDPI(NPI),  QPI(NPI), BP(NPI))                              ! SW 5/10/10
  ALLOCATE (IUPI(NPI),   IDPI(NPI),   EUPI(NPI),   EDPI(NPI),   WPI(NPI),    DLXPI(NPI),  FPI(NPI),    FMINPI(NPI), PUPIC(NPI))
  ALLOCATE (ETUPI(NPI),  EBUPI(NPI),  KTUPI(NPI),  KBUPI(NPI),  PDPIC(NPI),  ETDPI(NPI),  EBDPI(NPI),  KTDPI(NPI),  KBDPI(NPI))
  ALLOCATE (PUSPC(NSP),  ETUSP(NSP),  EBUSP(NSP),  KTUSP(NSP),  KBUSP(NSP),  PDSPC(NSP),  ETDSP(NSP),  EBDSP(NSP))
  ALLOCATE (KTDSP(NSP),  KBDSP(NSP),  IUSP(NSP),   IDSP(NSP),   ESP(NSP),    A1SP(NSP),   B1SP(NSP),   A2SP(NSP))
  ALLOCATE (B2SP(NSP),   AGASSP(NSP), BGASSP(NSP), CGASSP(NSP), EQSP(NSP),   GASSPC(NSP), JBUSP(NSP),  JBDSP(NSP))
  ALLOCATE (IUPU(NPU),   IDPU(NPU),   EPU(NPU),    STRTPU(NPU), ENDPU(NPU),  EONPU(NPU),  EOFFPU(NPU), QPU(NPU),   PPUC(NPU))
  ALLOCATE (ETPU(NPU),   EBPU(NPU),   KTPU(NPU),   KBPU(NPU),   JWUPU(NPU),  JWDPU(NPU),  JBUPU(NPU),  JBDPU(NPU), PUMPON(NPU))
  ALLOCATE (IWD(NWDT),   KWD(NWDT),   QWD(NWDT),   EWD(NWDT),   KTW(NWDT),   KBW(NWDT))
  ALLOCATE (ITR(NTRT),   QTRFN(NTR),  TTRFN(NTR),  CTRFN(NTR),  ELTRT(NTRT), ELTRB(NTRT), TRC(NTRT),   JBTR(NTRT), QTRF(KMX,NTRT))
  ALLOCATE (TTLB(IMX),   TTRB(IMX),   CLLB(IMX),   CLRB(IMX))
  ALLOCATE (SRLB1(IMX),  SRRB1(IMX),  SRLB2(IMX),  SRRB2(IMX),  SRFJD1(IMX), SHADEI(IMX), SRFJD2(IMX))
  ALLOCATE (TOPO(IMX,IANG))                                                                                        ! SW 10/17/05
  ALLOCATE (QSW(KMX,NWDT),  CTR(NCT,NTRT), HPRWBC(NHY,NWB))
  ALLOCATE (RATZ(KMX,NWB),   CURZ1(KMX,NWB),  CURZ2(KMX,NWB),   CURZ3(KMX,NWB))   ! SW 5/15/06
! v3.5 start
  ALLOCATE (zg(NZPt),zm(NZPt),zeff(NZPt),prefp(NZPt),zr(NZPt),zoomin(NZPt),zs2p(NZPt),exz(NZPt),PREFZ(NZPt,nzpt))
  ALLOCATE (zt1(NZPt),zt2(NZPt),zt3(NZPt),zt4(NZPt),zk1(NZPt),zk2(NZPt),zk3(NZPt),zk4(NZPt),o2zr(nzpt))
  ALLOCATE (zp(NZPt),zn(NZPt),zc(NZPt))
  allocate (prefa(nal,nzpt))
  allocate (po4zr(kmx,imx),nh4zr(kmx,imx))
  allocate (zmu(kmx,imx,nzp),tgraze(kmx,imx,nzp),zrt(kmx,imx,nzp),zmt(kmx,imx,nzp)) ! MLM POINTERS:,zoo(kmx,imx,NZP),zooss(kmx,imx,NZP))
  allocate (zoorm(kmx,imx,nzp),zoormr(kmx,imx,nzp),zoormf(kmx,imx,nzp))
  allocate (lpzooout(kmx,imx),lpzooin(kmx,imx),dozr(kmx,imx),ticzr(kmx,imx))
  allocate (agz(kmx,imx,nal,nzp), zgz(kmx,imx,nzp,nzp),agzt(kmx,imx,nal)) !omnivorous zooplankton
  allocate (ORGPLD(kmx,imx), ORGPRD(kmx,imx), ORGPLP(kmx,imx), ORGPRP(kmx,imx), ORGNLD(kmx,imx), ORGNRD(kmx,imx), ORGNLP(kmx,imx))
  allocate (ORGNRP(kmx,imx))
  allocate (ldompmp(kmx,imx),ldomnmp(kmx,imx),lpompmp(kmx,imx),lpomnmp(kmx,imx),rpompmp(kmx,imx),rpomnmp(kmx,imx))
  allocate (lpzooinp(kmx,imx),lpzooinn(kmx,imx),lpzoooutp(kmx,imx),lpzoooutn(kmx,imx))
  allocate (SEDVPp(KMX,NWB),SEDVPc(KMX,NWB),SEDVPn(KMX,NWB))
  allocate (sedp(kmx,imx),sedn(kmx,imx),sedc(kmx,imx))
  allocate (sdkv(kmx,imx),seddktot(kmx,imx))
  allocate (sedcip(nwb),sedcin(nwb),sedcic(nwb),sedcis(nwb))
  ALLOCATE (cbods(NBOD), cbodns(kmx,imx), sedcb(kmx,imx), sedcbp(kmx,imx), sedcbn(kmx,imx), sedcbc(kmx,imx))
  allocate  (print_macrophyte(nwb,nmct), macrophyte_calc(nwb,nmct),macwbc(nwb,nmct),conv2(kmx,kmx),mprwbc(nwb,nmct))
  allocate  (mac(kmx,imx,nmct), macrc(kmx,kmx,imx,nmct),mact(kmx,kmx,imx), macrm(kmx,kmx,imx,nmct), macss(kmx,kmx,imx,nmct))
  allocate  (mgr(kmx,kmx,imx,nmct),mmr(kmx,imx,nmct), mrr(kmx,imx,nmct))
  allocate  (smacrc(kmx,kmx,imx,nmct), smacrm(kmx,kmx,imx,nmct))
  allocate  (smact(kmx,kmx,imx), smac(kmx,imx,nmct))
  allocate  (mt1(nmct),mt2(nmct),mt3(nmct),mt4(nmct),mk1(nmct),mk2(nmct),mk3(nmct),mk4(nmct),mg(nmct),mr(nmct),mm(nmct))
  allocate  (mbmp(nmct), mmax(nmct), cddrag(nmct), dwv(nmct), dwsa(nmct), anorm(nmct))
  allocate  (mp(nmct), mn(nmct), mc(nmct),psed(nmct),nsed(nmct),mhsp(nmct),mhsn(nmct),mhsc(nmct),msat(nmct),exm(nmct))
  allocate  (O2MG(nmct), O2MR(nmct),  LRPMAC(nmct),  MPOM(nmct))
  allocate  (kticol(imx),armac(imx),macwbci(nwb,nmct))
  allocate  (macmbrs(nbr,nmct), macmbrt(nbr,nmct),ssmacmb(nbr,nmct))
  allocate  (cw(kmx,imx), bic(kmx,imx))
  allocate  (mactrmr(kmx,imx,nmct), mactrmf(kmx,imx,nmct),mactrm(kmx,imx,nmct))
  allocate  (mlfpr(kmx,kmx,imx,nmct))
  allocate  (mllim(kmx,kmx,imx,nmct), mplim(kmx,imx,nmct),mclim(kmx,imx,nmct),mnlim(kmx,imx,nmct))
  ALLOCATE  (GAMMAj(kmx,KMX,IMX))	
  allocate (por(kmx,imx),VOLKTi(imx),VOLi(Kmx,Imx),vstem(kmx,imx,nmct),vstemkt(imx,nmct),sarea(nmct))
  ALLOCATE (IWIND(NWB))
  ALLOCATE (LAYERCHANGE(NWB))
! v3.5 end
  allocate (cbodp(kmx,imx,nbod), cbodn(kmx,imx,nbod))      ! cb 6/6/10
! Allocate subroutine variables

  !CALL TRANSPORT
  !CALL KINETICS
  !CALL WATERBODY
  !CALL OPEN_CHANNEL_INITIALIZE
  !CALL PIPE_FLOW_INITIALIZE
  
  ! start specific allocations for control_file_read_write.f90 program
  allocate (hyd_prin(nhy),cst_icon(nct),cst_prin(nct))
  allocate  (cin_con(nct), ctr_con(nct), cdt_con(nct), cpr_con(nct))
  allocate  (gen_name(NGC),alg_name(NAL), ss_name(nss))
  allocate  (zoo_name(nzpt),epi_name(Nept),mac_name(nmct),bod_name(nbod))
  allocate  (tr_name(ntr),wb_name(nwb),br_name(nbr))
  allocate  (pipe_name(npi),spill_name(nsp), gate_name(ngt), pump_name(npu))
   ALLOCATE (BTHFNwrite(NWB), bthtype(nwb), bthline1(nwb),bthline2(nwb),vprfnwrite(nwb),vprtype(nwb),vprline1(nwb),vprline2(nwb))  
  zoo_name=blank;epi_name=blank; mac_name=blank; bod_name=blank  
! end specific allocations for control_file_read_write.f90 program

! State variables

  TDS  => C2(:,:,1);         PO4  => C2(:,:,NPO4);      NH4  => C2(:,:,NNH4);        NO3  => C2(:,:,NNO3);   DSI  => C2(:,:,NDSI)
  PSI  => C2(:,:,NPSI);      FE   => C2(:,:,NFE);       LDOM => C2(:,:,NLDOM);       RDOM => C2(:,:,NRDOM);  LPOM => C2(:,:,NLPOM)
  RPOM => C2(:,:,NRPOM);     O2   => C2(:,:,NDO);       TIC  => C2(:,:,NTIC);        ALK  => C2(:,:,NALK)
!  CG   => C2(:,:,NGCS:NGCE); SS   => C2(:,:,NSSS:NSSE); CBOD => C2(:,:,NBODS:NBODE); ALG  => C2(:,:,NAS:NAE)
  CG   => C2(:,:,NGCS:NGCE); SS   => C2(:,:,NSSS:NSSE); ALG  => C2(:,:,NAS:NAE)
  CBOD => C2(:,:,nbodcs:nbodce); CBODp => C2(:,:,nbodps:nbodpe)  ; CBODn => C2(:,:,nbodns:nbodne)      ! cb 6/6/10
! v3.5 start
  ZOO  => C2(:,:,NZOOS:NZOOE)
  LDOMP  => C2(:,:,nldomp); RDOMP  => C2(:,:,nrdomp); LPOMP  => C2(:,:,nlpomp); RPOMP  => C2(:,:,nrpomp)
  LDOMN  => C2(:,:,nldomN); RDOMN  => C2(:,:,nrdomn); LPOMN  => C2(:,:,nlpomn); RPOMN  => C2(:,:,nrpomn)
! v3.5 end

! State variable source/sinks

  CGSS   => CSSK(:,:,NGCS:NGCE);   SSSS   => CSSK(:,:,NSSS:NSSE); PO4SS  => CSSK(:,:,NPO4);  NH4SS  => CSSK(:,:,NNH4)
  NO3SS  => CSSK(:,:,NNO3);        DSISS  => CSSK(:,:,NDSI);      PSISS  => CSSK(:,:,NPSI);  FESS   => CSSK(:,:,NFE)
  LDOMSS => CSSK(:,:,NLDOM);       RDOMSS => CSSK(:,:,NRDOM);     LPOMSS => CSSK(:,:,NLPOM); RPOMSS => CSSK(:,:,NRPOM)
!  CBODSS => CSSK(:,:,NBODS:NBODE); ASS    => CSSK(:,:,NAS:NAE);   DOSS   => CSSK(:,:,NDO);   TICSS  => CSSK(:,:,NTIC)
  ASS    => CSSK(:,:,NAS:NAE);   DOSS   => CSSK(:,:,NDO);   TICSS  => CSSK(:,:,NTIC)  
  CBODss => Cssk(:,:,nbodcs:nbodce); CBODpss => Cssk(:,:,nbodps:nbodpe); CBODnss => Cssk(:,:,nbodns:nbodne)	  	      ! cb 6/6/10
! v3.5 start
  zooss  => cssk(:,:,NZOOS:NZOOE)
  LDOMPSS  => cssk(:,:,nldomp); RDOMPSS  => cssk(:,:,nrdomp); LPOMPSS  => cssk(:,:,nlpomp); RPOMPSS  => cssk(:,:,nrpomp)
  LDOMNSS  => cssk(:,:,nldomN); RDOMNSS  => cssk(:,:,nrdomn); LPOMNSS  => cssk(:,:,nlpomn); RPOMNSS  => cssk(:,:,nrpomn)
! v3.5 end

! Derived variables

  DOC   => CD(:,:,1);  POC  => CD(:,:,2);  TOC  => CD(:,:,3);  DON  => CD(:,:,4);  PON   => CD(:,:,5);  TON  => CD(:,:,6)
  TKN   => CD(:,:,7);  TN   => CD(:,:,8);  DOP  => CD(:,:,9);  POP  => CD(:,:,10); TOP   => CD(:,:,11); TP   => CD(:,:,12)
  APR   => CD(:,:,13); CHLA => CD(:,:,14); ATOT => CD(:,:,15); O2DG => CD(:,:,16); TOTSS => CD(:,:,17); TISS => CD(:,:,18)
  CBODU => CD(:,:,19); PH   => CD(:,:,20); CO2  => CD(:,:,21); HCO3 => CD(:,:,22); CO3   => CD(:,:,23)

! Kinetic fluxes

  SSSI   => KF(:,:,1);  SSSO   => KF(:,:,2);  PO4AR  => KF(:,:,3);  PO4AG  => KF(:,:,4);  PO4AP  => KF(:,:,5)
  PO4ER  => KF(:,:,6);  PO4EG  => KF(:,:,7);  PO4EP  => KF(:,:,8);  PO4POM => KF(:,:,9);  PO4DOM => KF(:,:,10)
  PO4OM  => KF(:,:,11); PO4SD  => KF(:,:,12); PO4SR  => KF(:,:,13); PO4NS  => KF(:,:,14); NH4D   => KF(:,:,15)
  NH4AR  => KF(:,:,16); NH4AG  => KF(:,:,17); NH4AP  => KF(:,:,18); NH4ER  => KF(:,:,19); NH4EG  => KF(:,:,20)
  NH4EP  => KF(:,:,21); NH4POM => KF(:,:,22); NH4DOM => KF(:,:,23); NH4OM  => KF(:,:,24); NH4SD  => KF(:,:,25)
  NH4SR  => KF(:,:,26); NO3D   => KF(:,:,27); NO3AG  => KF(:,:,28); NO3EG  => KF(:,:,29); NO3SED => KF(:,:,30)
  DSIAG  => KF(:,:,31); DSIEG  => KF(:,:,32); DSID   => KF(:,:,33); DSISD  => KF(:,:,34); DSISR  => KF(:,:,35)
  DSIS   => KF(:,:,36); PSIAM  => KF(:,:,37); PSINS  => KF(:,:,38); PSID   => KF(:,:,39); FENS   => KF(:,:,40)
  FESR   => KF(:,:,41); LDOMD  => KF(:,:,42); LRDOMD => KF(:,:,43); RDOMD  => KF(:,:,44); LDOMAP => KF(:,:,45)
  LDOMEP => KF(:,:,46); LPOMD  => KF(:,:,47); LRPOMD => KF(:,:,48); RPOMD  => KF(:,:,49); LPOMAP => KF(:,:,50)
  LPOMEP => KF(:,:,51); LPOMNS => KF(:,:,52); RPOMNS => KF(:,:,53); CBODDK => KF(:,:,54); DOAP   => KF(:,:,55)
  !DOEP   => KF(:,:,56); DOAR   => KF(:,:,57); DOER   => KF(:,:,58); DOPOM  => KF(:,:,59); DODOM  => KF(:,:,60)
  DOAR   => KF(:,:,56); DOEP   => KF(:,:,57); DOER   => KF(:,:,58); DOPOM  => KF(:,:,59); DODOM  => KF(:,:,60)   ! cb 6/2/2009
  DOOM   => KF(:,:,61); DONIT  => KF(:,:,62); DOBOD  => KF(:,:,63); DOAE   => KF(:,:,64); DOSED  => KF(:,:,65)
  DOSOD  => KF(:,:,66); TICAP  => KF(:,:,67); TICEP  => KF(:,:,68); SEDD   => KF(:,:,69); SEDAS  => KF(:,:,70)
  SEDOMS => KF(:,:,71); SEDNS  => KF(:,:,72); SODD   => KF(:,:,73)
! v3.5 start
  LDOMPAP => KF(:,:,74); LDOMPeP => KF(:,:,75); LPOMpAP => KF(:,:,76); LPOMPNS => KF(:,:,77); RPOMPNS => KF(:,:,78)
  LDOMnAP => KF(:,:,79); LDOMneP => KF(:,:,80); LPOMnAP => KF(:,:,81); LPOMnNS => KF(:,:,82); RPOMnNS => KF(:,:,83)
  SEDDp   => KF(:,:,84); SEDASp  => KF(:,:,85); SEDOMSp => KF(:,:,86); SEDNSp  => KF(:,:,87); lpomepp => KF(:,:,88)
  SEDDn   => KF(:,:,89); SEDASn  => KF(:,:,90); SEDOMSn => KF(:,:,91); SEDNSn  => KF(:,:,92); lpomepn => KF(:,:,93)
  SEDDc   => KF(:,:,94); SEDASc  => KF(:,:,95); SEDOMSc => KF(:,:,96); SEDNSc  => KF(:,:,97); lpomepc => KF(:,:,98)
  sedno3  => KF(:,:,99)
  po4mr   => KF(:,:,100);po4mg   => KF(:,:,101); nh4mr   => KF(:,:,102); nh4mg => KF(:,:,103); ldommac => KF(:,:,104)
  rpommac => KF(:,:,105);lpommac => KF(:,:,106); domp    => KF(:,:,107); domr  => KF(:,:,108); ticmc   => KF(:,:,109)
  cbodns  => KF(:,:,110);sedcb   => KF(:,:,111); sedcbp  => KF(:,:,112); sedcbn => KF(:,:,113); sedcbc  => KF(:,:,114)
  sedbr   => KF(:,:,115);sedbrp  => KF(:,:,116); sedbrn  => KF(:,:,117); sedbrc  => KF(:,:,118); co2reaer =>  KF(:,:,121)
! v3.5 end
  cbodnsp  => KF(:,:,119);cbodnsn  => KF(:,:,120)      ! cb 6/6/10

! Algal rate variables

  AGR => AF(:,:,:,1); ARR => AF(:,:,:,2); AER => AF(:,:,:,3); AMR => AF(:,:,:,4); ASR => AF(:,:,:,5)
  EGR => EF(:,:,:,1); ERR => EF(:,:,:,2); EER => EF(:,:,:,3); EMR => EF(:,:,:,4); EBR => EF(:,:,:,5)

! Hydrodynamic variables

  DLTLIM => HYD(:,:,1);  U   => HYD(:,:,2);  W    => HYD(:,:,3); T2   => HYD(:,:,4);  RHO => HYD(:,:,5);  AZ  => HYD(:,:,6)
  VSH    => HYD(:,:,7);  ST  => HYD(:,:,8);  SB   => HYD(:,:,9); ADMX => HYD(:,:,10); DM  => HYD(:,:,11); HDG => HYD(:,:,12)
  ADMZ   => HYD(:,:,13); HPG => HYD(:,:,14); GRAV => HYD(:,:,15)

! I/O units

  SNP => OPT(:,1); PRF => OPT(:,2); VPL => OPT(:,3); CPL => OPT(:,4); SPR => OPT(:,5); FLX => OPT(:,6);FLX2 => OPT(:,7)

! Zero variables

  ITR  = 0;   JBTR = 0;   KTTR = 0;   KBTR = 0;   QTR  = 0.0; TTR  = 0.0; CTR  = 0.0; QTRF = 0.0; SNPD  = 0.0; TSRD  = 0.0
  PRFD = 0.0; SPRD = 0.0; CPLD = 0.0; VPLD = 0.0; SCRD = 0.0; FLXD = 0.0; WDOD = 0.0; RSOD = 0.0; ELTRB = 0.0; ELTRT = 0.0

! Input file unit numbers

  NUNIT = 40
  DO JW=1,NWB
    BTH(JW) = NUNIT
    VPR(JW) = NUNIT+1
    LPR(JW) = NUNIT+2
    NUNIT   = NUNIT+3
  END DO
  GRF = NUNIT; NUNIT = NUNIT+1

! Time control cards

  READ (CON,'(//8X,2F8.0,I8)')         TMSTRT,   TMEND,    YEAR
  READ (CON,'(//8X,I8,F8.0,a8)')         NDLT,     DLTMIN, DLTINTER
  READ (CON,'(//(:8X,9F8.0))')        (DLTD(J),            J =1,NDLT)
  READ (CON,'(//(:8X,9F8.0))')        (DLTMAX(J),          J =1,NDLT)
  READ (CON,'(//(:8X,9F8.0))')        (DLTF(J),            J =1,NDLT)
  READ (CON,'(//(8X,2A8))')           (VISC(JW), CELC(JW), JW=1,NWB)

! Grid definition cards

  READ (CON,'(//(8X,7I8,F8.0,F8.0))') (US(JB),  DS(JB),     UHS(JB),   DHS(JB), UQB(JB), DQB(JB),  NL(JB), SLOPE(JB),SLOPEC(JB), JB=1,NBR)
  READ (CON,'(//(8X,3F8.0,3I8))')     (LAT(JW), LONGIT(JW), ELBOT(JW), BS(JW),  BE(JW),  JBDN(JW),                    JW=1,NWB)

! Initial condition cards

  READ (CON,'(//(8X,2F8.0,2A8))')     (T2I(JW),    ICETHI(JW),  WTYPEC(JW),  GRIDC(JW),                               JW=1,NWB)
  READ (CON,'(//(8X,6A8))')           (VBC(JW),    EBC(JW),     MBC(JW),     PQC(JW),   EVC(JW),   PRC(JW),           JW=1,NWB)
  READ (CON,'(//(8X,4A8))')           (WINDC(JW),  QINC(JW),    QOUTC(JW),   HEATC(JW),                               JW=1,NWB)
  READ (CON,'(//(8X,3A8))')           (QINIC(JB),  DTRIC(JB),   HDIC(JB),                                             JB=1,NBR)
  READ (CON,'(//(8X,5A8,4F8.0))')     (SLHTC(JW),  SROC(JW),    RHEVC(JW),   METIC(JW), FETCHC(JW), AFW(JW),                       &
                                       BFW(JW),    CFW(JW),     WINDH(JW),                                            JW=1,NWB)
  READ (CON,'(//(8X,2A8,6F8.0))')     (ICEC(JW),   SLICEC(JW),  ALBEDO(JW),  HWI(JW),   BETAI(JW),  GAMMAI(JW),                    &
                                       ICEMIN(JW), ICET2(JW),                                                         JW=1,NWB)
  READ (CON,'(//(8X,A8,F8.0))')       (SLTRC(JW),  THETA(JW),                                                         JW=1,NWB)
  READ (CON,'(//(8X,6F8.0,A8,F8.0))')      (AX(JW),     DXI(JW),     CBHE(JW),    TSED(JW),  FI(JW),     TSEDF(JW),                     &
                                       FRICC(JW), Z0(JW),                                                                    JW=1,NWB)
  READ (CON,'(//(8X,2A8,F8.0,I8,F8.0,F8.0,F8.0,F8.0,A8))')     (AZC(JW),    AZSLC(JW),   AZMAX(JW),   TKEBC(JW),EROUGH(JW),       &
                                       ARODI(JW),STRICK(JW),TKELATPRDCONST(JW),IMPTKE(JW),JW=1,NWB)          !,PHISET(JW

  DO JW=1,NWB
  IF(Z0(JW) <= 0.0)Z0(JW)=0.001      ! SW 11/28/07
   DO JB=BS(JW),BE(JW)
    DO I=US(JB),DS(JB)
    E(I)=EROUGH(JW)
    ENDDO
   ENDDO
  ENDDO

! Inflow-outflow cards

  READ (CON,'(//(8X,I8))')            (NSTR(JB),      JB=1,NBR)
  READ (CON,'(/)')
  DO JB=1,NBR
    READ (CON,'(:8X,9A8)')            (STRIC(JS,JB),  JS=1,NSTR(JB))
  END DO
  READ (CON,'(/)')
  DO JB=1,NBR
    READ (CON,'(:8X,9I8)')            (KTSWT(JS,JB), JS=1,NSTR(JB))
  END DO
  READ (CON,'(/)')
  DO JB=1,NBR
    READ (CON,'(:8X,9I8)')            (KBSWT(JS,JB), JS=1,NSTR(JB))
  END DO
  READ (CON,'(/)')
  DO JB=1,NBR
    READ (CON,'(:8X,9A8)')            (SINKCT(JS,JB),JS=1,NSTR(JB))
  END DO
  READ (CON,'(/)')
  DO JB=1,NBR
    READ (CON,'(:8X,9F8.0)')          (ESTRT(JS,JB), JS=1,NSTR(JB))
  END DO
  READ (CON,'(/)')
  DO JB=1,NBR
    READ (CON,'(:8X,9F8.0)')          (WSTRT(JS,JB), JS=1,NSTR(JB))
  END DO
  READ (CON,'(//(:a8,2I8,6F8.0,A8,A8))') (pipe_name(jp),IUPI(JP),   IDPI(JP),   EUPI(JP),   EDPI(JP),    WPI(JP),                                   &
                                       DLXPI(JP),  FPI(JP),    FMINPI(JP), LATPIC(JP),  DYNPIPE(JP),JP=1,NPI)
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PUPIC(JP),  ETUPI(JP),  EBUPI(JP),  KTUPI(JP),   KBUPI(JP),  JP=1,NPI)
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PDPIC(JP),  ETDPI(JP),  EBDPI(JP),  KTDPI(JP),   KBDPI(JP),  JP=1,NPI)
  READ (CON,'(//(:a8,2I8,5F8.0,A8))') (spill_name(js),IUSP(JS),   IDSP(JS),   ESP(JS),    A1SP(JS),    B1SP(JS),                                  &
                                       A2SP(JS),   B2SP(JS),   LATSPC(JS),                          JS=1,NSP)
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PUSPC(JS),  ETUSP(JS),  EBUSP(JS),  KTUSP(JS),   KBUSP(JS),  JS=1,NSP)
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PDSPC(JS),  ETDSP(JS),  EBDSP(JS),  KTDSP(JS),   KBDSP(JS),  JS=1,NSP)
  READ (CON,'(//(:8X,A8,I8,3F8.0))')  (GASSPC(JS), EQSP(JS),   AGASSP(JS), BGASSP(JS),  CGASSP(JS), JS=1,NSP)
  READ (CON,'(//(:a8,2I8,7F8.0,A8))') (gate_name(jg),IUGT(JG),   IDGT(JG),   EGT(JG),    A1GT(JG),    B1GT(JG),                                  &
                                       G1GT(JG),   A2GT(JG),   B2GT(JG),   G2GT(JG),    LATGTC(JG), JG=1,NGT)
 ! READ (CON,'(//(:8X,4F8.0,A8))')     (GTA1(JG),   GTB1(JG),   GTA2(JG),   GTB2(JG),    DYNGTC(JG), JG=1,NGT)
  READ (CON,'(//(:8X,4F8.0,2A8))')     (GTA1(JG),   GTB1(JG),   GTA2(JG),   GTB2(JG),    DYNGTC(JG),gtic(jg), JG=1,NGT)  ! cb 8/13/2010
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PUGTC(JG),  ETUGT(JG),  EBUGT(JG),  KTUGT(JG),   KBUGT(JG),  JG=1,NGT)
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PDGTC(JG),  ETDGT(JG),  EBDGT(JG),  KTDGT(JG),   KBDGT(JG),  JG=1,NGT)
  READ (CON,'(//(:8X,A8,I8,3F8.0))')  (GASGTC(JG), EQGT(JG),   AGASGT(JG), BGASGT(JG),  CGASGT(JG), JG=1,NGT)
  READ (CON,'(//(:a8,2I8,6F8.0,2A8))') (pump_name(jp),IUPU(JP),   IDPU(JP),   EPU(JP),    STRTPU(JP),  ENDPU(JP),                                 &
                                       EONPU(JP),  EOFFPU(JP), QPU(JP),    LATPUC(JP),  DYNPUMP(JP),      JP=1,NPU)
  READ (CON,'(//(:8X,A8,2F8.0,2I8))') (PPUC(JP),   ETPU(JP),   EBPU(JP),   KTPU(JP),    KBPU(JP),   JP=1,NPU)
  READ (CON,'(//(:8X,9I8))')          (IWR(JW),    JW=1,NIW)
  READ (CON,'(//(:8X,9I8))')          (KTWR(JW),   JW=1,NIW)
  READ (CON,'(//(:8X,9I8))')          (KBWR(JW),   JW=1,NIW)
  READ (CON,'(//(:8X,9A8))')          (WDIC(JW),   JW=1,NWD)
  READ (CON,'(//(:8X,9I8))')          (IWD(JW),    JW=1,NWD)
  READ (CON,'(//(:8X,9F8.0))')        (EWD(JW),    JW=1,NWD)
  READ (CON,'(//(:8X,9I8))')          (KTWD(JW),   JW=1,NWD)
  READ (CON,'(//(:8X,9I8))')          (KBWD(JW),   JW=1,NWD)
  READ (CON,'(//(:8X,9A8))')          (TRC(JT),    JT=1,NTR)
  READ (CON,'(//(:8X,9A8))')          (TRIC(JT),   JT=1,NTR)
  READ (CON,'(//(:8X,9I8))')          (ITR(JT),    JT=1,NTR)
  READ (CON,'(//(:8X,9F8.0))')        (ELTRT(JT),  JT=1,NTR)
  READ (CON,'(//(:8X,9F8.0))')        (ELTRB(JT),  JT=1,NTR)
  READ (CON,'(//(8X,A8))')            (DTRC(JB),   JB=1,NBR)

! Output control cards (excluding constituents)

  READ (CON,'(/)')
  DO JH=1,NHY
    READ (CON,'(10a8:/(:8x,9a8))')            hyd_prin(jh),(HPRWBC(JH,JW),JW=1,NWB)
  END DO
  READ (CON,'(//(8X,A8,2I8))')        (SNPC(JW), NSNP(JW), NISNP(JW), JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (SNPD(J,JW),J=1,NSNP(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (SNPF(J,JW),J=1,NSNP(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9I8)')            (ISNP(I,JW),I=1,NISNP(JW))
  END DO
  READ (CON,'(//(8X,A8,I8))')         (SCRC(JW), NSCR(JW), JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (SCRD(J,JW),J=1,NSCR(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (SCRF(J,JW),J=1,NSCR(JW))
  END DO
  READ (CON,'(//(8X,A8,2I8))')        (PRFC(JW), NPRF(JW), NIPRF(JW), JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (PRFD(J,JW),J=1,NPRF(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (PRFF(J,JW),J=1,NPRF(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9I8)')            (IPRF(J,JW),J=1,NIPRF(JW))
  END DO
  READ (CON,'(//(8X,A8,2I8))')        (SPRC(JW), NSPR(JW), NISPR(JW), JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (SPRD(J,JW),J=1,NSPR(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (SPRF(J,JW),J=1,NSPR(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9I8)')            (ISPR(J,JW), J=1,NISPR(JW))
  END DO
  READ (CON,'(//(8X,A8,I8))')         (VPLC(JW),  NVPL(JW),  JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (VPLD(J,JW), J=1,NVPL(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (VPLF(J,JW), J=1,NVPL(JW))
  END DO
  READ (CON,'(//(8X,A8,I8,A8))')      (CPLC(JW),   NCPL(JW), TECPLOT(JW),JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (CPLD(J,JW), J=1,NCPL(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (CPLF(J,JW), J=1,NCPL(JW))
  END DO
  READ (CON,'(//(8X,A8,I8))')         (FLXC(JW),   NFLX(JW), JW=1,NWB)
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (FLXD(J,JW), J=1,NFLX(JW))
  END DO
  READ (CON,'(/)')
  DO JW=1,NWB
    READ (CON,'(:8X,9F8.0)')          (FLXF(J,JW), J=1,NFLX(JW))
  END DO
  READ (CON,'(//8X,A8,2I8)')           TSRC,    NTSR,    NIKTSR; ALLOCATE (ITSR(MAX(1,NIKTSR)), ETSR(MAX(1,NIKTSR)))
  READ (CON,'(//(:8X,9F8.0))')        (TSRD(J), J=1,NTSR)
  READ (CON,'(//(:8X,9F8.0))')        (TSRF(J), J=1,NTSR)
  READ (CON,'(//(:8X,9I8))')          (ITSR(J), J=1,NIKTSR)
  READ (CON,'(//(:8X,9F8.0))')        (ETSR(J), J=1,NIKTSR)
  READ (CON,'(//8X,A8,2I8)')           WDOC,    NWDO,    NIWDO;  ALLOCATE (IWDO(MAX(1,NIWDO)))
  READ (CON,'(//(:8X,9F8.0))')        (WDOD(J), J=1,NWDO)
  READ (CON,'(//(:8X,9F8.0))')        (WDOF(J), J=1,NWDO)
  READ (CON,'(//(8X,9I8))')           (IWDO(J), J=1,NIWDO)
  READ (CON,'(//8X,A8,I8,A8)')         RSOC,    NRSO,    RSIC
  READ (CON,'(//(:8X,9F8.0))')        (RSOD(J), J=1,NRSO)
  READ (CON,'(//(:8X,9F8.0))')        (RSOF(J), J=1,NRSO)

! Constituent control cards

  READ (CON,'(//8X,2A8,I8)')           CCC, LIMC, CUF
  READ (CON,'(//(2A8))')              (CNAME2(JC),  CAC(JC),      JC=1,NCT)
  READ (CON,'(/)')
  DO JD=1,NDC
    READ (CON,'(A8,(:9A8))')           CDNAME2(JD),(CDWBC(JD,JW), JW=1,NWB)
  END DO
  READ (CON,'(/)')
  KFNAME2 ='     '   ! SW 9/27/13 INITIALIZE ENTIRE ARRAY
  KFWBC   ='     '   ! SW 9/27/13 INITIALIZE ENTIRE ARRAY
!  DO JF=1,NFL
  do jf=1,73   ! Fix this later
    if(nwb < 10)READ (CON,'(A8,(:9A8))')         KFNAME2(JF),(KFWBC(JF,JW),  JW=1,NWB)
    if(nwb >= 10)READ (CON,'(A8,9A8,/(:8X,9A8))')         KFNAME2(JF),(KFWBC(JF,JW),  JW=1,NWB)          !cb 9/13/12  sw2/18/13  Foramt 6/16/13 8/13/13
  END DO
  kfname2(121) = 'CO2GASX'
  READ (CON,'(/)')
  DO JC=1,NCT
    READ (CON,'(:a8,9F8.0)')          cst_icon(jc),(C2I(JC,JW),    JW=1,NWB)
  END DO
  READ (CON,'(/)')
  DO JC=1,NCT
    READ (CON,'(:a8,9A8)')            cst_prin(jc),(CPRWBC(JC,JW), JW=1,NWB)
  END DO
  READ (CON,'(/)')
  DO JC=1,NCT
    READ (CON,'(:a8,9A8)')            cin_con(jc),(CINBRC(JC,JB), JB=1,NBR)
  END DO
  READ (CON,'(/)')
  DO JC=1,NCT
    READ (CON,'(:a8,9A8)')            ctr_con(jc),(CTRTRC(JC,JT), JT=1,NTR)
  END DO
  READ (CON,'(/)')
  DO JC=1,NCT
    READ (CON,'(:a8,9A8)')            cdt_con(jc),(CDTBRC(JC,JB), JB=1,NBR)
  END DO
  READ (CON,'(/)')
  DO JC=1,NCT
    READ (CON,'(:a8,9A8)')            cpr_con(jc),(CPRBRC(JC,JB), JB=1,NBR)
  END DO

! Kinetics coefficients

  READ (CON,'(//(8X,4F8.0,2A8))')     (EXH2O(JW),  EXSS(JW),   EXOM(JW),   BETA(JW),   EXC(JW),   EXIC(JW),    JW=1,NWB)
  READ (CON,'(//(8X,9F8.0))')         (EXA(JA),                                                                JA=1,NAL)
  READ (CON,'(//(8X,9F8.0))')         (EXZ(Jz),                                                                Jz=1,Nzpt)  !v3.5
  READ (CON,'(//(8X,9F8.0))')         (EXM(Jm),                                                                Jm=1,nmct)  !v3.5
  !READ (CON,'(//(a8,4F8.0))')         (gen_name(Jg),CGQ10(JG),  CG0DK(JG),  CG1DK(JG),  CGS(JG),                            JG=1,NGC)
  READ (CON,'(//(a8,7F8.0))')         (gen_name(Jg),CGQ10(JG),  CG0DK(JG),  CG1DK(JG),  CGS(JG), CGLDK(JG),CGKLF(JG),CGCS(JG),           JG=1,NGC) !LCJ 2/26/15
  READ (CON,'(//(a8,F8.0,A,F8.0))')   (ss_name(js),SSS(JS),    SEDRC(JS),  TAUCR(JS),                                      JS=1,NSS)
  READ (CON,'(//(a8,9F8.0))')         (alg_name(ja),AG(JA),     AR(JA),     AE(JA),     AM(JA),     AS(JA),                                     &
                                       AHSP(JA),   AHSN(JA),   AHSSI(JA),  ASAT(JA),                           JA=1,NAL)
  READ (CON,'(//(8X,8F8.0))')         (AT1(JA),    AT2(JA),    AT3(JA),    AT4(JA),    AK1(JA),   AK2(JA),                         &
                                       AK3(JA),    AK4(JA),                                                    JA=1,NAL)
  READ (CON,'(//(8X,6F8.0,I8,F8.0))') (AP(JA),     AN(JA),     AC(JA),     ASI(JA),    ACHLA(JA), APOM(JA),                        &
                                       ANEQN(JA),  ANPR(JA),   JA=1,NAL)
  READ (CON,'(//(8X,9A8))')           (EPIC(JW,1),                                                             JW=1,NWB)
  DO JE=2,NEPT
    READ (CON,'(8X,9A8)')             (EPIC(JW,JE),                                                            JW=1,NWB)
  END DO
  READ (CON,'(//(8X,9A8))')           (EPIPRC(JW,1),                                                           JW=1,NWB)
  DO JE=2,NEPT
    READ (CON,'(8X,9A8)')             (EPIPRC(JW,JE),                                                          JW=1,NWB)
  END DO
  READ (CON,'(//(8X,9F8.0))')         (EPICI(JW,1),                                                            JW=1,NWB)
  DO JE=2,NEPT
    READ (CON,'(8X,9F8.0)')           (EPICI(JW,JE),                                                           JW=1,NWB)
  END DO
  READ (CON,'(//(a8,8F8.0))')         (epi_name(je),EG(JE),     ER(JE),     EE(JE),     EM(JE),     EB(JE),    EHSP(JE),                        &
                                       EHSN(JE),   EHSSI(JE),                                                  JE=1,NEP)
  READ (CON,'(//(8X,2F8.0,I8,F8.0))') (ESAT(JE),   EHS(JE),    ENEQN(JE),  ENPR(JE),                           JE=1,NEP)
  READ (CON,'(//(8X,8F8.0))')         (ET1(JE),    ET2(JE),    ET3(JE),    ET4(JE),    EK1(JE),   EK2(JE),                         &
                                       EK3(JE),    EK4(JE),                                                    JE=1,NEP)
  READ (CON,'(//(8X,6F8.0))')         (EP(JE),     EN(JE),     EC(JE),     ESI(JE),    ECHLA(JE), EPOM(JE),    JE=1,NEP)
! v3.5 start
  READ (CON,'(//(a8,7F8.0))')         (zoo_name(jz),zg(jz),zr(jz),zm(jz),zeff(jz),PREFP(jz),ZOOMIN(jz),ZS2P(jz),            Jz=1,Nzpt)

  READ (CON,'(//(8X,8F8.0))')         (PREFA(ja,1),                                                            Ja=1,nal)          ! MM 7/13/06
  do jz=2,nzpt
    READ (CON,'((8X,8F8.0))')       (PREFA(ja,jz),                                                           Ja=1,nal)
  end do
  READ (CON,'(//(8X,8F8.0))')       (PREFz(jjz,1),                                                          Jjz=1,nzpt)
  do jz=2,nzpt
    READ (CON,'((8X,8F8.0))')       (PREFz(jjz,jz),                                                          Jjz=1,nzpt)           ! MM 7/13/06
  end do
  READ (CON,'(//(8X,8F8.0))')         (zT1(Jz),    zT2(Jz),    zT3(Jz),    zT4(Jz),    zK1(Jz),   zK2(Jz),                         &
                                       zK3(Jz),    zK4(Jz),                                                    Jz=1,Nzpt)
  READ (CON,'(//(8X,3F8.0))')         (zP(Jz),     zN(Jz),     zC(Jz),                                         Jz=1,Nzpt)
  READ (CON,'(//(8X,9A8))')           (macwbC(JW,1),                                                           JW=1,NWB)
  DO Jm=2,nmct
    READ (CON,'(8X,9A8)')             (macwbC(JW,Jm),                                                          JW=1,NWB)
  END DO
  READ (CON,'(//(8X,9A8))')           (mprwbC(JW,1),                                                           JW=1,NWB)
  DO Jm=2,nmct
    READ (CON,'(8X,9A8)')             (mprwbC(JW,Jm),                                                          JW=1,NWB)
  END DO
  READ (CON,'(//(8X,9F8.0))')         (macwbCI(JW,1),                                                          JW=1,NWB)
  DO Jm=2,nmct
    READ (CON,'(8X,9F8.0)')           (macwbcI(JW,Jm),                                                         JW=1,NWB)
  END DO
  READ (CON,'(//(a8,9F8.0))')         (mac_name(jm),mG(jm), mR(jm), mM(jm), msat(jm),mhsp(jm),mhsn(jm),mhsc(jm),                           &
                                          mpom(jm),lrpmac(jm),     jm=1,nmct)
  READ (CON,'(//(8X,2F8.0))')         (psed(jm), nsed(jm),                                                     jm=1,nmct)
  READ (CON,'(//(8X,2F8.0))')         (mbmp(jm), mmax(jm),                                                     jm=1,nmct)
  READ (CON,'(//(8X,4F8.0))')         (cddrag(jm),dwv(jm),dwsa(jm),anorm(jm),                                 jm=1,nmct)  !cb 6/29/06
  READ (CON,'(//(8X,8F8.0))')         (mT1(Jm),    mT2(Jm),    mT3(Jm),    mT4(Jm),    mK1(Jm),   mK2(Jm),                         &
                                       mK3(Jm),    mK4(Jm),                                                    Jm=1,nmct)
  READ (CON,'(//(8X,3F8.0))')         (mP(Jm),     mN(Jm),     mC(Jm),                                         Jm=1,nmct)
  READ (CON,'(//(8X,3F8.0))')         (LDOMDK(JW), RDOMDK(JW), LRDDK(JW),                                      JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (LPOMDK(JW), RPOMDK(JW), LRPDK(JW),  POMS(JW),                           JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (ORGP(JW),   ORGN(JW),   ORGC(JW),   ORGSI(JW),                          JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (OMT1(JW),   OMT2(JW),   OMK1(JW),   OMK2(JW),                           JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (KBOD(JB),   TBOD(JB),   RBOD(JB), cbods(jb),                           JB=1,NBOD)  !v3.5
  READ (CON,'(//(8X,3F8.0))')         (BODP(JB),   BODN(JB),   BODC(JB),                                       JB=1,NBOD)
  READ (CON,'(//(8X,2F8.0))')         (PO4R(JW),   PARTP(JW),                                                  JW=1,NWB)
  READ (CON,'(//(8X,2F8.0))')         (NH4R(JW),   NH4DK(JW),                                                  JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (NH4T1(JW),  NH4T2(JW),  NH4K1(JW),  NH4K2(JW),                          JW=1,NWB)
  READ (CON,'(//(8X,3F8.0))')         (NO3DK(JW),  NO3S(JW),   FNO3SED(JW),                                    JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (NO3T1(JW),  NO3T2(JW),  NO3K1(JW),  NO3K2(JW),                          JW=1,NWB)
  READ (CON,'(//(8X,4F8.0))')         (DSIR(JW),   PSIS(JW),   PSIDK(JW),  PARTSI(JW),                         JW=1,NWB)
  READ (CON,'(//(8X,2F8.0))')         (FER(JW),    FES(JW),                                                    JW=1,NWB)
  READ (CON,'(//(8X,F8.0))')          (CO2R(JW),                                                               JW=1,NWB)
  READ (CON,'(//(8X,2F8.0))')         (O2NH4(JW),  O2OM(JW),                                                   JW=1,NWB)
  READ (CON,'(//(8X,2F8.0))')         (O2AR(JA),   O2AG(JA),                                                   JA=1,NAL)
  READ (CON,'(//(8X,2F8.0))')         (O2ER(JE),   O2EG(JE),                                                   JE=1,NEPT)
  READ (CON,'(//(8X,F8.0))')          (O2zR(Jz),                                                               Jz=1,Nzpt)
  READ (CON,'(//(8X,2F8.0))')         (O2mR(Jm),   O2mG(jm),                                                   Jm=1,nmct)
  READ (CON,'(//(8X,F8.0))')           KDO
  READ (CON,'(//(8X,2A8,6F8.0,A8))')     (SEDCc(JW),   SEDPRC(JW), SEDCI(JW),  SDK(JW), seds(jw),   FSOD(JW),   FSED(JW), sedb(jw),DYNSEDK(JW),   JW=1,NWB)  ! cb 11/28/06
  READ (CON,'(//(8X,4F8.0))')         (SODT1(JW),  SODT2(JW),  SODK1(JW),  SODK2(JW),                          JW=1,NWB)
  READ (CON,'(//(8X,9F8.0))')         (SOD(I),                                                                  I=1,IMX)
  READ (CON,'(//(8X,A8,I8,4F8.2))')   (REAERC(JW), NEQN(JW),   RCOEF1(JW), RCOEF2(JW), RCOEF3(JW), RCOEF4(JW), JW=1,NWB)

! Input filenames

  READ (CON,'(//(8X,A72))')  RSIFN
  READ (CON,'(//(8X,A72))')  QWDFN
  READ (CON,'(//(8X,A72))')  QGTFN
  READ (CON,'(//(8X,A72))')  WSCFN
  READ (CON,'(//(8X,A72))')  SHDFN
  READ (CON,'(//(8X,A72))') (BTHFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (METFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (EXTFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (VPRFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (LPRFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (QINFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (TINFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (CINFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (QOTFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (QTRFN(JT), JT=1,NTR)
  READ (CON,'(//(8X,A72))') (TTRFN(JT), JT=1,NTR)
  READ (CON,'(//(8X,A72))') (CTRFN(JT), JT=1,NTR)
  READ (CON,'(//(8X,A72))') (QDTFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (TDTFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (CDTFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (PREFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (TPRFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (CPRFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (EUHFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (TUHFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (CUHFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (EDHFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (TDHFN(JB), JB=1,NBR)
  READ (CON,'(//(8X,A72))') (CDHFN(JB), JB=1,NBR)

! Output filenames

  READ (CON,'(//(8X,A72))') (SNPFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (PRFFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (VPLFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (CPLFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (SPRFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))') (FLXFN(JW), JW=1,NWB)
  READ (CON,'(//(8X,A72))')  TSRFN
  READ (CON,'(//(8X,A72))')  WDOFN
  CLOSE (CON)

! Bathymetry file

  DO JW=1,NWB
    OPEN (BTH(JW),FILE=BTHFN(JW),STATUS='OLD')
	READ  (BTH(JW),'(a1)')char1                                 ! New Bathymetry format option SW 6/22/09
      IF(CHAR1=='$')THEN
      READ  (BTH(JW),*)
      READ  (BTH(JW),*) AID,(DLX(I),  I=US(BS(JW))-1,DS(BE(JW))+1)
      READ  (BTH(JW),*) AID,(ELWS(I), I=US(BS(JW))-1,DS(BE(JW))+1)
      READ  (BTH(JW),*) AID,(PHI0(I), I=US(BS(JW))-1,DS(BE(JW))+1)
      READ  (BTH(JW),*) AID,(FRIC(I), I=US(BS(JW))-1,DS(BE(JW))+1)
      READ  (BTH(JW),*)
      DO K=1,KMX
      READ  (BTH(JW),*) H(K,JW),(B(K,I),I=US(BS(JW))-1,DS(BE(JW))+1)
      END DO
      DO I=US(BS(JW))-1,DS(BE(JW))+1
      H2(:,I) = H(:,JW)
      END DO
      ELSE	
    rewind(bth(jw))
    read(bth(jw),'(a)')bthline1
    read(bth(jw),'(a)')bthline2
    read(bth(jw),*)
    READ (BTH(JW),'((10F8.0))') (DLX(I),  I=US(BS(JW))-1,DS(BE(JW))+1)
    READ (BTH(JW),'(//(10F8.0))') (ELWS(I), I=US(BS(JW))-1,DS(BE(JW))+1)
    READ (BTH(JW),'(//(10F8.0))') (PHI0(I), I=US(BS(JW))-1,DS(BE(JW))+1)
    READ (BTH(JW),'(//(10F8.0))') (FRIC(I), I=US(BS(JW))-1,DS(BE(JW))+1)
    READ (BTH(JW),'(//(10F8.0))') (H(K,JW), K=1,KMX)
    DO I=US(BS(JW))-1,DS(BE(JW))+1
      READ (BTH(JW),'(//(10F8.0))') (B(K,I), K=1,KMX)
      H2(:,I) = H(:,JW)
    END DO
	endif
    CLOSE (BTH(JW))
  END DO
  H1 = H2
  BI = B

  ALLOCATE(BSAVE(KMX,IMX))
  BSAVE=0.0
  BSAVE = B
  
  
  ! Initialize logical control variables
  CONSTITUENTS =  CCC  == '      ON'
  VERT_PROFILE = .FALSE.  
  DO JW=1,NWB    
    VERT_TEMP(JW)        = T2I(JW)     == -1    
    IF(CONSTITUENTS)THEN                     ! CB 12/04/08    
    VERT_SEDIMENT(JW)    = SEDCI(JW)   == -1.0 .AND. SEDCC(JW)   == '      ON'    
    VERT_EPIPHYTON(JW,:) = EPICI(JW,:) == -1.0 .AND. EPIC(JW,:) == '      ON'
    DO JC=1,NCT      
      VERT_CONC(JC,JW) = C2I(JC,JW) == -1.0 .AND. CAC(JC) == '      ON'      
      IF (VERT_CONC(JC,JW)) VERT_PROFILE(JW) = .TRUE.      
    END DO
    IF (VERT_SEDIMENT(JW))         VERT_PROFILE(JW) = .TRUE.    
    IF (ANY(VERT_EPIPHYTON(JW,:))) VERT_PROFILE(JW) = .TRUE.    
    END IF                          ! cb 12/04/08
    IF (VERT_TEMP(JW))             VERT_PROFILE(JW) = .TRUE.    
  END DO
  PH_CALC               = CONSTITUENTS .AND. CDWBC(20,:) == '      ON'
  IZMIN  = 0;   KTWB   = 2;   KMIN   = 1;   IMIN   = 1; KBMAX  = 0
  
  ! Convert slope to angle alpha in radians

  ALPHA = ATAN(SLOPE)
  SINA  = SIN(ALPHA)
  SINAC = SIN(ATAN(SLOPEC))
  COSA  = COS(ALPHA)
  
  ! Water surface and bottom layers - need ktwb and kbmax to read vpr files
    
  NPOINT = 0
  DO JW=1,NWB
    IF (ZERO_SLOPE(JW)) THEN
      DO I=US(BS(JW))-1,DS(BE(JW))+1
        EL(KMX,I) = ELBOT(JW)
        DO K=KMX-1,1,-1
          EL(K,I) = EL(K+1,I)+H(K,JW)
        END DO
      END DO
    ELSE
      EL(KMX,DS(JBDN(JW))+1) = ELBOT(JW)
      JB                     = JBDN(JW)
      NPOINT(JB)             = 1
      NNBP                   = 1
      NCBP                   = 0
      NINTERNAL              = 0
      NUP                    = 0
      DO WHILE (NNBP <= (BE(JW)-BS(JW)+1))
        NCBP = NCBP+1
        IF (NINTERNAL == 0) THEN
          IF (NUP == 0) THEN
            DO I=DS(JB),US(JB),-1
              IF (I /= DS(JB)) THEN
                EL(KMX,I) = EL(KMX,I+1)+SINA(JB)*(DLX(I)+DLX(I+1))*0.5
              ELSE
                EL(KMX,I) = EL(KMX,I+1)
              END IF
              DO K=KMX-1,1,-1
                EL(K,I) = EL(K+1,I)+H(K,JW)*COSA(JB)
              END DO
            END DO
          ELSE
            DO I=US(JB),DS(JB)
              IF (I /= US(JB)) THEN
                EL(KMX,I) = EL(KMX,I-1)-SINA(JB)*(DLX(I)+DLX(I-1))*0.5
              ELSE
                EL(KMX,I) = EL(KMX,I-1)
              END IF
              DO K=KMX-1,1,-1
                EL(K,I) = EL(K+1,I)+H(K,JW)*COSA(JB)
              END DO
            END DO
            NUP = 0
          END IF
          DO K=KMX,1,-1
            IF (UP_HEAD(JB)) THEN
              EL(K,US(JB)-1) = EL(K,US(JB))+SINA(JB)*DLX(US(JB))
            ELSE
              EL(K,US(JB)-1) = EL(K,US(JB))
            END IF
            IF (DN_HEAD(JB)) THEN
              EL(K,DS(JB)+1) = EL(K,DS(JB))-SINA(JB)*DLX(DS(JB))
            ELSE
              EL(K,DS(JB)+1) = EL(K,DS(JB))
            END IF
          END DO
        ELSE
          DO K=KMX-1,1,-1
            EL(K,UHS(JJB)) = EL(K+1,UHS(JJB))+H(K,JW)*COSA(JB)
          END DO
          DO I=UHS(JJB)+1,DS(JB)
            EL(KMX,I) = EL(KMX,I-1)-SINA(JB)*(DLX(I)+DLX(I-1))*0.5
            DO K=KMX-1,1,-1
              EL(K,I) = EL(K+1,I)+H(K,JW)*COSA(JB)
            END DO
          END DO
          DO I=UHS(JJB)-1,US(JB),-1
            EL(KMX,I) = EL(KMX,I+1)+SINA(JB)*(DLX(I)+DLX(I+1))*0.5
            DO K=KMX-1,1,-1
              EL(K,I) = EL(K+1,I)+H(K,JW)*COSA(JB)
            END DO
          END DO
          NINTERNAL = 0
        END IF
        IF (NNBP == (BE(JW)-BS(JW)+1)) EXIT

!****** Find next branch connected to furthest downstream branch

        DO JB=BS(JW),BE(JW)
          IF (NPOINT(JB) /= 1) THEN
            DO JJB=BS(JW),BE(JW)
              IF (DHS(JB) >= US(JJB) .AND. DHS(JB) <=DS (JJB) .AND. NPOINT(JJB) == 1) THEN
                NPOINT(JB)       = 1
                EL(KMX,DS(JB)+1) = EL(KMX,DHS(JB))+SINA(JB)*(DLX(DS(JB))+DLX(DHS(JB)))*0.5
                NNBP             = NNBP+1; EXIT
              END IF
              IF (UHS(JJB) == DS(JB) .AND. NPOINT(JJB) == 1) THEN
                NPOINT(JB)       = 1
                EL(KMX,DS(JB)+1) = EL(KMX,US(JJB))+(SINA(JJB)*DLX(US(JJB))+SINA(JB)*DLX(DS(JB)))*0.5
                NNBP             = NNBP+1; EXIT
              END IF
              IF (UHS(JJB) >= US(JB) .AND. UHS(JJB) <= DS(JB) .AND. NPOINT(JJB)==1) THEN
                NPOINT(JB)       = 1
                EL(KMX,UHS(JJB)) = EL(KMX,US(JJB))+SINA(JJB)*DLX(US(JJB))*0.5
                NNBP             = NNBP+1
                NINTERNAL        = 1; EXIT
              END IF
              IF (UHS(JB) >= US(JJB) .AND. UHS(JB) <= DS(JJB) .AND. NPOINT(JJB) == 1) THEN
                NPOINT(JB)       = 1
                EL(KMX,US(JB)-1) = EL(KMX,UHS(JB))-SINA(JB)*DLX(US(JB))*0.5
                NNBP             = NNBP+1
                NUP              = 1; EXIT
              END IF
            END DO
            IF (NPOINT(JB)==1) EXIT
          END IF
        END DO
      END DO
    END IF
  END DO

! Minimum/maximum layer heights

  DO JW=1,NWB
    DO K=KMX-1,1,-1
      HMIN = DMIN1(H(K,JW),HMIN)
      HMAX = DMAX1(H(K,JW),HMAX)
    END DO
  END DO
  HMAX2 = HMAX**2


  DO JW=1,NWB
    DO JB=BS(JW),BE(JW)
      DO I=US(JB)-1,DS(JB)+1        
          KTI(I) = 2
          DO WHILE (EL(KTI(I),I) > ELWS(I))
            KTI(I) = KTI(I)+1
            if(kti(i) == kmx)then  ! cb 7/7/2010 if elws below grid, setting to elws to just within grid
              kti(i)=2
              elws(i)=el(kti(i),i)
              exit
            end if
          END DO
          Z(I)     = (EL(KTI(I),I)-ELWS(I))/COSA(JB)
          ZMIN(JW) = DMAX1(ZMIN(JW),Z(I))
          KTMAX    =  MAX(2,KTI(I))
          KTWB(JW) =  MAX(KTMAX,KTWB(JW))         
          KTI(I)   =  MAX(KTI(I)-1,2)
          IF (Z(I) > ZMIN(JW)) IZMIN(JW) = I        
        K = 2
        DO WHILE (B(K,I) > 0.0)
          KB(I) = K
          K     = K+1
        END DO
        KBMAX(JW) = MAX(KBMAX(JW),KB(I))
      END DO
      KB(US(JB)-1) = KB(US(JB))
      KB(DS(JB)+1) = KB(DS(JB))
    END DO


!** Correct for water surface going over several layers

    
      KT = KTWB(JW)
      DO JB=BS(JW),BE(JW)
        DO I=US(JB)-1,DS(JB)+1                   
          H2(KT,I) = H(KT,JW)-Z(I)
          K        = KTI(I)+1          
          DO WHILE (KT > K)
            Z(I)     = Z(I)-H(K,JW)
            H2(KT,I) = H(KT,JW)-Z(I)
            K        = K+1           
          END DO
        END DO
      END DO    
    ELKT(JW) = EL(KTWB(JW),DS(BE(JW)))-Z(DS(BE(JW)))*COSA(BE(JW))
  END DO
  
  vprline1=blank
  vprline2=blank
  
  ! vertical profile files
  DO JW=1,NWB
    KT = KTWB(JW)
    IF (VERT_PROFILE(JW)) THEN

!**** Temperature and water quality

      OPEN (VPR(JW),FILE=VPRFN(JW),STATUS='OLD')
      READ (VPR(JW),'(A1)')vprtype(jw)
 
      IF(vprtype(jw)=='$')THEN
          READ( VPR(JW),'(/)')
!          READ( VPR(JW),'(a)')linetest
         IF (VERT_TEMP(JW)) READ (VPR(JW),*) IBLANK, (TVP(K,JW),K=KT,KBMAX(JW))
         IF (CONSTITUENTS) THEN
          DO JC=1,NCT
           IF (VERT_CONC(JC,JW))      READ (VPR(JW),*) IBLANK, (CVP(K,JC,JW),  K=KT,KBMAX(JW))
          END DO
          DO JE=1,NEP
            IF (VERT_EPIPHYTON(JW,JE)) READ (VPR(JW),*) IBLANK, (EPIVP(K,JW,JE),K=KT,KBMAX(JW))
          END DO
          IF (VERT_SEDIMENT(JW))       READ (VPR(JW),*) IBLANK,(SEDVP(K,JW),   K=KT,KBMAX(JW))
          END IF
      ELSE
         rewind(vpr(jw))
         read(vpr(jw),'(a)')vprline1
         read(vpr(jw),'(a)')vprline2                
         IF (VERT_TEMP(JW)) READ (VPR(JW),'(/(8X,9F8.0))') (TVP(K,JW),K=KT,KBMAX(JW))
         IF (CONSTITUENTS) THEN
           DO JC=1,NCT
            IF (VERT_CONC(JC,JW))      READ (VPR(JW),'(//(8X,9F8.0))') (CVP(K,JC,JW),  K=KT,KBMAX(JW))
           END DO
           DO JE=1,NEP
            IF (VERT_EPIPHYTON(JW,JE)) READ (VPR(JW),'(//(8X,9F8.0))') (EPIVP(K,JW,JE),K=KT,KBMAX(JW))
           END DO
           IF (VERT_SEDIMENT(JW))       READ (VPR(JW),'(//(8X,9F8.0))') (SEDVP(K,JW),   K=KT,KBMAX(JW))
          END IF
      ENDIF
      close(vpr(jw))
    END IF            
  end do
  
  TIME_SERIES           = TSRC        == '      ON'
  DOWNSTREAM_OUTFLOW = WDOC   == '      ON'
  SEDIMENT_CALC         = CONSTITUENTS .AND. SEDCc        == '      ON'
  ICE_CALC              = ICEC        == '      ON'
  ICE_COMPUTATION       = ANY(ICE_CALC)
  DERIVED_CALC          = CONSTITUENTS .AND. ANY(CDWBC   == '      ON')
  VECTOR             = VPLC   == '      ON'
  
  NAC    = 0;   NTAC   = 0;   NACD   = 0;   NACIN  = 0;   NACTR  = 0;   NACDT  = 0;   NACPR  = 0; NAF    = 0
  IF (CONSTITUENTS) THEN
    DO JC=1,NCT
      IF (CAC(JC) == '      ON') THEN
        NAC     = NAC+1
        CN(NAC) = JC
      END IF
      DO JB=1,NBR
        IF (CINBRC(JC,JB) == '      ON') THEN
          NACIN(JB)       = NACIN(JB)+1
          INCN(NACIN(JB),JB) = JC
        END IF
        IF (CDTBRC(JC,JB) == '      ON') THEN
          NACDT(JB)       = NACDT(JB)+1
          DTCN(NACDT(JB),JB) = JC
        END IF
        IF (CPRBRC(JC,JB) == '      ON') THEN
          NACPR(JB)       = NACPR(JB)+1
          PRCN(NACPR(JB),JB) = JC
        END IF
      END DO
      DO JT=1,NTR
        IF (CTRTRC(JC,JT) == '      ON') THEN
          NACTR(JT)          = NACTR(JT)+1
          TRCN(NACTR(JT),JT) = JC
        END IF
      END DO
    END DO
    DO JW=1,NWB
      DO JD=1,NDC
        IF (CDWBC(JD,JW) == '      ON') THEN
          NACD(JW)         = NACD(JW)+1
          CDN(NACD(JW),JW) = JD
        END IF
      END DO
      DO JF=1,NFL
        IF (KFWBC(JF,JW) == '      ON') THEN
          NAF(JW)          = NAF(JW)+1
          KFCN(NAF(JW),JW) = JF
        elseif(ph_calc(jw).and. jf==121)then
          NAF(JW)          = NAF(JW)+1
          KFCN(NAF(JW),JW) = JF       
        END IF
      END DO
    END DO
  END IF
  
  return
end


!**************************************************************
	subroutine write_control_file40

  USE MAIN
    USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use initialvelocity
  use extra

  character*1 ibr1, iwb1, itr1
  character*2 ibr2, iwb2, itr2
  character*3, ibr3, iwb3, itr3
  CHARACTER(8)  :: IBLANK
  iblank="        "

  open(con,file=CONFNwrite,status='unknown')

! creating branch, water body, and tributary line labels
  do jb=1,nbr
    if(jb.le.9)then    
	  write(ibr1,'(i1)')jb
      br_name(jb)='br'//ibr1
	end if
	if(jb.ge.10.and.jb.le.99)then    
	  write(ibr2,'(i2)')jb
      br_name(jb)='br'//ibr2
	end if
	if(jb.ge.100.and.jb.le.999)then    
	  write(ibr3,'(i3)')jb
      br_name(jb)='br'//ibr3
	end if
  end do

  do jw=1,nwb
    if(jw.le.9)then    
	  write(iwb1,'(i1)')jw
      wb_name(jw)='wb'//iwb1
	end if
	if(jw.ge.10.and.jw.le.99)then    
	  write(iwb2,'(i2)')jw
      wb_name(jw)='wb'//iwb2
	end if
	if(jw.ge.100.and.jw.le.999)then    
	  write(iwb3,'(i3)')jw
      wb_name(jw)='wb'//iwb3
	end if
  end do

  do jt=1,ntr
    if(jt.le.9)then    
	  write(itr1,'(i1)')jt
      tr_name(jt)='tr'//itr1
	end if
	if(jt.ge.10.and.jt.le.99)then    
	  write(itr2,'(i2)')jt
      tr_name(jt)='tr'//itr2
	end if
	if(jt.ge.100.and.jt.le.999)then    
	  write(itr3,'(i3)')jt
      tr_name(jt)='tr'//itr3
	end if
  end do

! Title and array dimensions
  
  write(con,'("  PSU CE-QUAL-W2 Model Version 3.7",/)')
  write(con,'("TITLE C ...............................TITLE....................................")')
  write (CON,'(8X,A72)') (TITLE(J),J=1,10)
  write(con,'(/,"GRID         NWB     NBR     IMX     KMX   NPROC  CLOSEC")')
  write (CON,'(8X,5I8,a8)')     NWB, NBR, IMX, KMX,NPROC,CLOSEC
  write(con,'(/,"IN/OUTFL     NTR     NST     NIW     NWD     NGT     NSP     NPI     NPU")')
  write (CON,'(8X,8I8)')     NTR, NST, NIW, NWD, NGT, NSP, NPI, NPU
  write(con,'(/,"CONSTITU     NGC     NSS     NAL     NEP    NBOD     NMC     NZP")')
  write (CON,'(8X,7I8,a8)')  NGC, NSS, NAL, NEP, NBOD, nmc, nzp  !v3.5
  write(con,'(/,"MISCELL     NDAY SELECTC HABTATC ENVIRPC AERATEC INITUWL  SYSTDG   N2BND   DOBND")')
  write (CON,'(8X,I8,8a8)')      NOD,SELECTC,HABTATC,ENVIRPC,AERATEC,INITUWL,SYSTDG,N2BND,DOBND

! Time control cards

  write(con,'(/,"TIME CON  TMSTRT   TMEND    YEAR")')
  write (CON,'(8X,2f8.2,I8)')         TMSTRT,   TMEND,    YEAR
  write(con,'(/,"DLT CON      NDT  DLTMIN DLTINTR")')
  write (CON,'(8X,I8,f8.3,a8)')         NDLT,     DLTMIN, DLTINTER
  write(con,'(/,"DLT DATE    DLTD    DLTD    DLTD    DLTD    DLTD    DLTD    DLTD    DLTD    DLTD")')
  write (CON,'(:8X,9f8.2)')        (DLTD(J),            J =1,NDLT)
  write(con,'(/,"DLT MAX   DLTMAX  DLTMAX  DLTMAX  DLTMAX  DLTMAX  DLTMAX  DLTMAX  DLTMAX  DLTMAX")')
  write (CON,'(:8X,9F8.2)')        (DLTMAX(J),          J =1,NDLT)
  write(con,'(/,"DLT FRN     DLTF    DLTF    DLTF    DLTF    DLTF    DLTF    DLTF    DLTF    DLTF")')
  write (CON,'(:8X,9F8.2)')        (DLTF(J),            J =1,NDLT)
  write(con,'(/,"DLT LIMI    VISC    CELC")')
  write (CON,'(a8,2A8)')           (wb_name(jw),VISC(JW), CELC(JW), JW=1,NWB)

! Grid definition cards

  write(con,'(/,"BRANCH G      US      DS     UHS     DHS     UQB     DQB   NLMIN   SLOPE  SLOPEC")')
  write (CON,'(a8,7I8,2F8.5)')      (br_name(jb),US(JB),  DS(JB),     UHS(JB),   DHS(JB), UQB(JB), DQB(JB),  NL(JB), SLOPE(JB),SLOPEC(JB), JB=1,NBR)
  write(con,'(/,"LOCATION     LAT    LONG    EBOT      BS      BE    JBDN")')
  write (CON,'(a8,2f8.3,f8.3,3I8)')     (wb_name(jw),LAT(JW), LONGIT(JW), ELBOT(JW), BS(JW),  BE(JW),  JBDN(JW),                    JW=1,NWB)

! Initial condition cards

  write(con,'(/,"INIT CND     T2I    ICEI  WTYPEC   GRIDC")')
  write (CON,'(a8,2F8.3,2A8)')     (wb_name(jw),T2I(JW),    ICETHI(JW),  WTYPEC(JW),  GRIDC(JW),                               JW=1,NWB)
  write(con,'(/,"CALCULAT     VBC     EBC     MBC     PQC     EVC     PRC")')
  write (CON,'(a8,6A8)')           (wb_name(jw),VBC(JW),    EBC(JW),     MBC(JW),     PQC(JW),   EVC(JW),   PRC(JW),           JW=1,NWB)
  write(con,'(/,"DEAD SEA   WINDC    QINC   QOUTC   HEATC")')
  write (CON,'(a8,4A8)')           (wb_name(jw),WINDC(JW),  QINC(JW),    QOUTC(JW),   HEATC(JW),                               JW=1,NWB)
  write(con,'(/,"INTERPOL   QINIC   DTRIC    HDIC")')
  write (CON,'(a8,3A8)')           (br_name(jb),QINIC(JB),  DTRIC(JB),   HDIC(JB),                                             JB=1,NBR)
  write(con,'(/,"HEAT EXCH  SLHTC    SROC  RHEVAP   METIC  FETCHC     AFW     BFW     CFW   WINDH")')
  write (CON,'(a8,5A8,3F8.4,f8.3)')     (wb_name(jw),SLHTC(JW),  SROC(JW),    RHEVC(JW),   METIC(JW), FETCHC(JW), AFW(JW),                       &
                                       BFW(JW),    CFW(JW),     WINDH(JW),                                            JW=1,NWB)
  write(con,'(/,"ICE COVE    ICEC  SLICEC  ALBEDO   HWICE    BICE    GICE  ICEMIN   ICET2")')
  write (CON,'(a8,2A8,6F8.4)')     (wb_name(jw),ICEC(JW),   SLICEC(JW),  ALBEDO(JW),  HWI(JW),   BETAI(JW),  GAMMAI(JW),                    &
                                       ICEMIN(JW), ICET2(JW),                                                         JW=1,NWB)
  write(con,'(/,"TRANSPOR   SLTRC   THETA")')
  write (CON,'(a8,A8,F8.5)')       (wb_name(jw),SLTRC(JW),  THETA(JW),                                                         JW=1,NWB)
  write(con,'(/,"HYD COEF      AX      DX    CBHE    TSED      FI   TSEDF   FRICC      Z0")')
  write (CON,'(a8,6F8.4,A8,F8.4)')      (wb_name(jw),AX(JW),     DXI(JW),     CBHE(JW),    TSED(JW),  FI(JW),     TSEDF(JW),                     &
                                       FRICC(JW), Z0(JW),                                                                    JW=1,NWB)
  write(con,'(/,"EDDY VISC    AZC   AZSLC   AZMAX     FBC       E   ARODI STRCKLR BOUNDFR  TKECAL")')
  write (CON,'(a8,2A8,F8.4,I8,4F8.4,A8)')     (wb_name(jw),AZC(JW),    AZSLC(JW),   AZMAX(JW),   TKEBC(JW),EROUGH(JW),       &
                                       ARODI(JW),STRICK(JW),TKELATPRDCONST(JW),IMPTKE(JW),JW=1,NWB)          !,PHISET(JW  

! Inflow-outflow cards

  write(con,'(/,"N STRUC     NSTR")')
  write (CON,'(a8,I8)')            (br_name(jb),NSTR(JB),      JB=1,NBR)
  write(con,'(/,"STR INT    STRIC   STRIC   STRIC   STRIC   STRIC   STRIC   STRIC   STRIC   STRIC")')
  DO JB=1,NBR
    write (CON,'(:a8,9A8)')            br_name(jb),(STRIC(JS,JB),  JS=1,NSTR(JB))
  END DO
  write(con,'(/,"STR TOP    KTSTR   KTSTR   KTSTR   KTSTR   KTSTR   KTSTR   KTSTR   KTSTR   KTSTR")')
  DO JB=1,NBR
    write (CON,'(:a8,9I8)')            br_name(jb),(KTSWT(JS,JB), JS=1,NSTR(JB))
  END DO
  write(con,'(/,"STR BOT    KBSTR   KBSTR   KBSTR   KBSTR   KBSTR   KBSTR   KBSTR   KBSTR   KBSTR")')
  DO JB=1,NBR
    write (CON,'(:a8,9I8)')            br_name(jb),(KBSWT(JS,JB), JS=1,NSTR(JB))
  END DO
  write(con,'(/,"STR SINK   SINKC   SINKC   SINKC   SINKC   SINKC   SINKC   SINKC   SINKC   SINKC")')
  DO JB=1,NBR
    write (CON,'(:a8,9A8)')            br_name(jb),(SINKCT(JS,JB),JS=1,NSTR(JB))
  END DO
  write(con,'(/,"STR ELEV    ESTR    ESTR    ESTR    ESTR    ESTR    ESTR    ESTR    ESTR    ESTR")')
  DO JB=1,NBR
    write (CON,'(:a8,9F8.3)')          br_name(jb),(ESTRT(JS,JB), JS=1,NSTR(JB))
  END DO
  write(con,'(/,"STR WIDT    WSTR    WSTR    WSTR    WSTR    WSTR    WSTR    WSTR    WSTR    WSTR")')
  DO JB=1,NBR
    write (CON,'(:a8,9F8.2)')          br_name(jb),(WSTRT(JS,JB), JS=1,NSTR(JB))
  END DO
  write(con,'(/,"PIPES       IUPI    IDPI    EUPI    EDPI     WPI   DLXPI     FPI  FMINPI   WTHLC")')
  write (CON,'(:a8,2I8,6F8.3,A8)') (pipe_name(jp),IUPI(JP),   IDPI(JP),   EUPI(JP),   EDPI(JP),    WPI(JP),                                   &
                                       DLXPI(JP),  FPI(JP),    FMINPI(JP), LATPIC(JP),              JP=1,NPI)
  write(con,'(/,"PIPE UP    PUPIC   ETUPI   EBUPI   KTUPI   KBUPI")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (pipe_name(jp),PUPIC(JP),  ETUPI(JP),  EBUPI(JP),  KTUPI(JP),   KBUPI(JP),  JP=1,NPI)
  write(con,'(/,"PIPE DOWN  PDPIC   ETDPI   EBDPI   KTDPI   KBDPI")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (pipe_name(jp),PDPIC(JP),  ETDPI(JP),  EBDPI(JP),  KTDPI(JP),   KBDPI(JP),  JP=1,NPI)
  write(con,'(/,"SPILLWAY    IUSP    IDSP     ESP    A1SP    B1SP    A2SP    B2SP   WTHLC")')
  write (CON,'(:a8,2I8,5F8.3,A8)') (spill_name(js),IUSP(JS),   IDSP(JS),   ESP(JS),    A1SP(JS),    B1SP(JS),                                  &
                                       A2SP(JS),   B2SP(JS),   LATSPC(JS),                          JS=1,NSP)
  write(con,'(/,"SPILL UP   PUSPC   ETUSP   EBUSP   KTUSP   KBUSP")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (spill_name(js),PUSPC(JS),  ETUSP(JS),  EBUSP(JS),  KTUSP(JS),   KBUSP(JS),  JS=1,NSP)
  write(con,'(/,"SPILL DOWN PDSPC   ETUSP   EBUSP   KTDSP   KBDSP")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (spill_name(js),PDSPC(JS),  ETDSP(JS),  EBDSP(JS),  KTDSP(JS),   KBDSP(JS),  JS=1,NSP)
  write(con,'(/,"SPILL GAS GASSPC    EQSP  AGASSP  BGASSP  CGASSP")')
  write (CON,'(:a8,A8,I8,2F8.3,f8.4)')  (spill_name(js),GASSPC(JS), EQSP(JS),   AGASSP(JS), BGASSP(JS),  CGASSP(JS), JS=1,NSP)
  write(con,'(/,"GATES       IUGT    IDGT     EGT    A1GT    B1GT    G1GT    A2GT    B2GT    G2GT   WTHLC")')
  write (CON,'(:a8,2I8,f8.3,6F8.2,A8)') (gate_name(jg),IUGT(JG),   IDGT(JG),   EGT(JG),    A1GT(JG),    B1GT(JG),                                  &
                                       G1GT(JG),   A2GT(JG),   B2GT(JG),   G2GT(JG),    LATGTC(JG), JG=1,NGT)
  write(con,'(/,"GATE WEIR   GTA1    GTB1    GTA2    GTB2  DYNVAR    GTIC")')
  write (CON,'(:a8,4F8.2,2A8)')     (gate_name(jg),GTA1(JG),   GTB1(JG),   GTA2(JG),   GTB2(JG),    DYNGTC(JG),gtic(jg), JG=1,NGT)
  write(con,'(/,"GATE UP    PUGTC   ETUGT   EBUGT   KTUGT   KBUGT")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (gate_name(jg),PUGTC(JG),  ETUGT(JG),  EBUGT(JG),  KTUGT(JG),   KBUGT(JG),  JG=1,NGT)
  write(con,'(/,"GATE DOWN  PDGTC   ETDGT   EBDGT   KTDGT   KBDGT")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (gate_name(jg),PDGTC(JG),  ETDGT(JG),  EBDGT(JG),  KTDGT(JG),   KBDGT(JG),  JG=1,NGT)
  write(con,'(/,"GATE GAS  GASGTC    EQGT  AGASGT  BGASGT  CGASGT")')
  write (CON,'(:a8,A8,I8,f8.3,2F8.2)')  (gate_name(jg),GASGTC(JG), EQGT(JG),   AGASGT(JG), BGASGT(JG),  CGASGT(JG), JG=1,NGT)
  write(con,'(/,"PUMPS 1     IUPU    IDPU     EPU  STRTPU   ENDPU   EONPU  EOFFPU     QPU   WTHLC DYNPUMP")')
  write (CON,'(:a8,2I8,6F8.3,2A8)') (pump_name(jp),IUPU(JP),   IDPU(JP),   EPU(JP),    STRTPU(JP),  ENDPU(JP),                                 &
                                       EONPU(JP),  EOFFPU(JP), QPU(JP),    LATPUC(JP), DYNPUMP(JP),     JP=1,NPU)
  write(con,'(/,"PUMPS 2     PPUC    ETPU    EBPU    KTPU    KBPU")')
  write (CON,'(:a8,A8,2F8.3,2I8)') (pump_name(jp),PPUC(JP),   ETPU(JP),   EBPU(JP),   KTPU(JP),    KBPU(JP),   JP=1,NPU)
  write(con,'(/,"WEIR SEG     IWR     IWR     IWR     IWR     IWR     IWR     IWR     IWR     IWR")')
  write (CON,'(:8X,9I8)')          (IWR(JW),    JW=1,NIW)
  write(con,'(/,"WEIR TOP    KTWR    KTWR    KTWR    KTWR    KTWR    KTWR    KTWR    KTWR    KTWR")')
  write (CON,'(:8X,9I8)')          (KTWR(JW),   JW=1,NIW)
  write(con,'(/,"WEIR BOT    KBWR    KBWR    KBWR    KBWR    KBWR    KBWR    KBWR    KBWR    KBWR")')
  write (CON,'(:8X,9I8)')          (KBWR(JW),   JW=1,NIW)
  write(con,'(/,"WD INT      WDIC    WDIC    WDIC    WDIC    WDIC    WDIC    WDIC    WDIC    WDIC")')
  write (CON,'(:8X,9A8)')          (WDIC(JW),   JW=1,NWD)
  write(con,'(/,"WD SEG       IWD     IWD     IWD     IWD     IWD     IWD     IWD     IWD     IWD")')
  write (CON,'(:8X,9I8)')          (IWD(JW),    JW=1,NWD)
  write(con,'(/,"WD ELEV      EWD     EWD     EWD     EWD     EWD     EWD     EWD     EWD     EWD")')
  write (CON,'(:8X,9F8.3)')        (EWD(JW),    JW=1,NWD)
  write(con,'(/,"WD TOP      KTWD    KTWD    KTWD    KTWD    KTWD    KTWD    KTWD    KTWD    KTWD")')
  write (CON,'(:8X,9I8)')          (KTWD(JW),   JW=1,NWD)
  write(con,'(/,"WD BOT      KBWD    KBWD    KBWD    KBWD    KBWD    KBWD    KBWD    KBWD    KBWD")')
  write (CON,'(:8X,9I8)')          (KBWD(JW),   JW=1,NWD)
  write(con,'(/,"TRIB PLA    PTRC    PTRC    PTRC    PTRC    PTRC    PTRC    PTRC    PTRC    PTRC")')
  write (CON,'(:8x,9A8)')          (TRC(JT),    JT=1,NTR)
  write(con,'(/,"TRIB INT    TRIC    TRIC    TRIC    TRIC    TRIC    TRIC    TRIC    TRIC    TRIC")')
  write (CON,'(:8x,9A8)')          (TRIC(JT),   JT=1,NTR)
  write(con,'(/,"TRIB SEG     ITR     ITR     ITR     ITR     ITR     ITR     ITR     ITR     ITR")')
  write (CON,'(:8x,9I8)')          (ITR(JT),    JT=1,NTR)
  write(con,'(/,"TRIB TOP   ELTRT   ELTRT   ELTRT   ELTRT   ELTRT   ELTRT   ELTRT   ELTRT   ELTRT")')
  write (CON,'(:8x,9F8.3)')        (ELTRT(JT),  JT=1,NTR)
  write(con,'(/,"TRIB BOT   ELTRB   ELTRB   ELTRB   ELTRB   ELTRB   ELTRB   ELTRB   ELTRB   ELTRB")')
  write (CON,'(:8x,9F8.0)')        (ELTRB(JT),  JT=1,NTR)
  write(con,'(/,"DST TRIB    DTRC    DTRC    DTRC    DTRC    DTRC    DTRC    DTRC    DTRC    DTRC")')
  write (CON,'(a8,A8)')            (br_name(jb),DTRC(JB),   JB=1,NBR)

! Output control cards (excluding constituents)

  write(con,'(/,"HYD PRIN  HPRWBC  HPRWBC  HPRWBC  HPRWBC  HPRWBC  HPRWBC  HPRWBC  HPRWBC  HPRWBC")')
  DO JH=1,NHY
    write (CON,'(10a8:/(:8x,9a8))')            hyd_prin(jh),(HPRWBC(JH,JW),JW=1,NWB)
  END DO
  write(con,'(/,"SNP PRINT   SNPC    NSNP   NISNP")')
  write (CON,'(a8,A8,2I8)')        (wb_name(jw),SNPC(JW), NSNP(JW), NISNP(JW), JW=1,NWB)
  write(con,'(/,"SNP DATE    SNPD    SNPD    SNPD    SNPD    SNPD    SNPD    SNPD    SNPD    SNPD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(SNPD(J,JW),J=1,NSNP(JW))
  END DO
  write(con,'(/,"SNP FREQ    SNPF    SNPF    SNPF    SNPF    SNPF    SNPF    SNPF    SNPF    SNPF")')
  DO JW=1,NWB
    !write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(SNPF(J,JW),J=1,NSNP(JW))
      write (CON,'(a8,9f8.6:/(:8x,9F8.6))')          wb_name(jw),(SNPF(J,JW),J=1,NSNP(JW))
  END DO
  write(con,'(/,"SNP SEG     ISNP    ISNP    ISNP    ISNP    ISNP    ISNP    ISNP    ISNP    ISNP")')
  DO JW=1,NWB
    write (CON,'(a8,9i8:/(:8x,9I8))')            wb_name(jw),(ISNP(I,JW),I=1,NISNP(JW))
  END DO
  write(con,'(/,"SCR PRINT   SCRC    NSCR")')
  write (CON,'(a8,A8,I8)')         (wb_name(jw),SCRC(JW), NSCR(JW), JW=1,NWB)
  write(con,'(/,"SCR DATE    SCRD    SCRD    SCRD    SCRD    SCRD    SCRD    SCRD    SCRD    SCRD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(SCRD(J,JW),J=1,NSCR(JW))
  END DO
  write(con,'(/,"SCR FREQ    SCRF    SCRF    SCRF    SCRF    SCRF    SCRF    SCRF    SCRF    SCRF")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(SCRF(J,JW),J=1,NSCR(JW))
  END DO
  write(con,'(/,"PRF PLOT    PRFC    NPRF   NIPRF")')
  write (CON,'(a8,A8,2I8)')        (wb_name(jw),PRFC(JW), NPRF(JW), NIPRF(JW), JW=1,NWB)
  write(con,'(/,"PRF DATE    PRFD    PRFD    PRFD    PRFD    PRFD    PRFD    PRFD    PRFD    PRFD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(PRFD(J,JW),J=1,NPRF(JW))
  END DO
  write(con,'(/,"PRF FREQ    PRFF    PRFF    PRFF    PRFF    PRFF    PRFF    PRFF    PRFF    PRFF")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(PRFF(J,JW),J=1,NPRF(JW))
  END DO
  write(con,'(/,"PRF SEG     IPRF    IPRF    IPRF    IPRF    IPRF    IPRF    IPRF    IPRF    IPRF")')
  DO JW=1,NWB
    write (CON,'(a8,9i8:/(:8x,9i8))')            wb_name(jw),(IPRF(J,JW),J=1,NIPRF(JW))
  END DO
  write(con,'(/,"SPR PLOT    SPRC    NSPR   NISPR")')
  write (CON,'(a8,A8,2I8)')        (wb_name(jw),SPRC(JW), NSPR(JW), NISPR(JW), JW=1,NWB)
  write(con,'(/,"SPR DATE    SPRD    SPRD    SPRD    SPRD    SPRD    SPRD    SPRD    SPRD    SPRD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(SPRD(J,JW),J=1,NSPR(JW))
  END DO
  write(con,'(/,"SPR FREQ    SPRF    SPRF    SPRF    SPRF    SPRF    SPRF    SPRF    SPRF    SPRF")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(SPRF(J,JW),J=1,NSPR(JW))
  END DO
  write(con,'(/,"SPR SEG     ISPR    ISPR    ISPR    ISPR    ISPR    ISPR    ISPR    ISPR    ISPR")')
  DO JW=1,NWB
    write (CON,'(a8,9i8:/(:8x,9i8))')            wb_name(jw),(ISPR(J,JW), J=1,NISPR(JW))
  END DO
  write(con,'(/,"VPL PLOT    VPLC    NVPL")')
  write (CON,'(a8,A8,I8)')         (wb_name(jw),VPLC(JW),  NVPL(JW),  JW=1,NWB)
  write(con,'(/,"VPL DATE    VPLD    VPLD    VPLD    VPLD    VPLD    VPLD    VPLD    VPLD    VPLD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(VPLD(J,JW), J=1,NVPL(JW))
  END DO
  write(con,'(/,"VPL FREQ    VPLF    VPLF    VPLF    VPLF    VPLF    VPLF    VPLF    VPLF    VPLF")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(VPLF(J,JW), J=1,NVPL(JW))
  END DO
  write(con,'(/,"CPL PLOT    CPLC    NCPL TECPLOT")')
  write (CON,'(a8,A8,I8,A8)')      (wb_name(jw),CPLC(JW),   NCPL(JW), TECPLOT(JW),JW=1,NWB)
  write(con,'(/,"CPL DATE    CPLD    CPLD    CPLD    CPLD    CPLD    CPLD    CPLD    CPLD    CPLD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(CPLD(J,JW), J=1,NCPL(JW))
  END DO
  write(con,'(/,"CPL FREQ    CPLF    CPLF    CPLF    CPLF    CPLF    CPLF    CPLF    CPLF    CPLF")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(CPLF(J,JW), J=1,NCPL(JW))
  END DO
  write(con,'(/,"FLUXES      FLXC    NFLX")')
  write (CON,'(a8,A8,I8)')         (wb_name(jw),FLXC(JW),   NFLX(JW), JW=1,NWB)
  write(con,'(/,"FLX DATE    FLXD    FLXD    FLXD    FLXD    FLXD    FLXD    FLXD    FLXD    FLXD")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.2:/(:8x,9F8.2))')          wb_name(jw),(FLXD(J,JW), J=1,NFLX(JW))
  END DO
  write(con,'(/,"FLX FREQ    FLXF    FLXF    FLXF    FLXF    FLXF    FLXF    FLXF    FLXF    FLXF")')
  DO JW=1,NWB
    write (CON,'(a8,9f8.3:/(:8x,9F8.3))')          wb_name(jw),(FLXF(J,JW), J=1,NFLX(JW))
  END DO
  write(con,'(/,"TSR PLOT    TSRC    NTSR   NITSR")')
  write (CON,'(8X,A8,2I8)')           TSRC,    NTSR,    NIKTSR
  write(con,'(/,"TSR DATE    TSRD    TSRD    TSRD    TSRD    TSRD    TSRD    TSRD    TSRD    TSRD")')
  write (CON,'(:8X,9F8.2)')        (TSRD(J), J=1,NTSR)
  write(con,'(/,"TSR FREQ    TSRF    TSRF    TSRF    TSRF    TSRF    TSRF    TSRF    TSRF    TSRF")')
  !write (CON,'(:8X,9F8.3)')        (TSRF(J), J=1,NTSR)
  write (CON,'(:8X,9F8.5)')        (TSRF(J), J=1,NTSR)
  write(con,'(/,"TSR SEG     ITSR    ITSR    ITSR    ITSR    ITSR    ITSR    ITSR    ITSR    ITSR")')
  write (CON,'(:8X,9I8)')          (ITSR(J), J=1,NIKTSR)
  write(con,'(/,"TSR LAYE    ETSR    ETSR    ETSR    ETSR    ETSR    ETSR    ETSR    ETSR    ETSR")')
  write (CON,'(:8X,9F8.3)')        (ETSR(J), J=1,NIKTSR)
  write(con,'(/,"WITH OUT    WDOC    NWDO   NIWDO")')
  write (CON,'(8X,A8,2I8)')           WDOC,    NWDO,    NIWDO
  write(con,'(/,"WITH DAT    WDOD    WDOD    WDOD    WDOD    WDOD    WDOD    WDOD    WDOD    WDOD")')
  write (CON,'(:8X,9F8.2)')        (WDOD(J), J=1,NWDO)
  write(con,'(/,"WITH FRE    WDOF    WDOF    WDOF    WDOF    WDOF    WDOF    WDOF    WDOF    WDOF")')
  !write (CON,'(:8X,9F8.3)')        (WDOF(J), J=1,NWDO)
  write (CON,'(:8X,9f8.6)')        (WDOF(J), J=1,NWDO)
  write(con,'(/,"WITH SEG    IWDO    IWDO    IWDO    IWDO    IWDO    IWDO    IWDO    IWDO    IWDO")')
  write (CON,'(8X,9I8)')           (IWDO(J), J=1,NIWDO)
  write(con,'(/,"RESTART     RSOC    NRSO    RSIC")')
  write (CON,'(8X,A8,I8,A8)')         RSOC,    NRSO,    RSIC
  write(con,'(/,"RSO DATE    RSOD    RSOD    RSOD    RSOD    RSOD    RSOD    RSOD    RSOD    RSOD")')
  write (CON,'(:8X,9F8.2)')        (RSOD(J), J=1,NRSO)
  write(con,'(/,"RSO FREQ    RSOF    RSOF    RSOF    RSOF    RSOF    RSOF    RSOF    RSOF    RSOF")')
  write (CON,'(:8X,9F8.3)')        (RSOF(J), J=1,NRSO)

! Constituent control cards

  write(con,'(/,"CST COMP     CCC    LIMC     CUF")')
  write (CON,'(8X,2A8,I8)')           CCC, LIMC, CUF
  write(con,'(/,"CST ACTIVE   CAC")')
  write (CON,'(2A8)')              (CNAME2(JC),  CAC(JC),      JC=1,NCT)
  write(con,'(/,"CST DERI   CDWBC   CDWBC   CDWBC   CDWBC   CDWBC   CDWBC   CDWBC   CDWBC   CDWBC")')
  DO JD=1,NDC
    write (CON,'(10a8:/(:8x,9a8))')           CDNAME2(JD),(CDWBC(JD,JW), JW=1,NWB)
  END DO
  write(con,'(/,"CST FLUX   CFWBC   CFWBC   CFWBC   CFWBC   CFWBC   CFWBC   CFWBC   CFWBC   CFWBC")')
!  DO JF=1,NFL
  do jf=1,73   ! Fix this later
   ! write (CON,'(10a8:/(:8x,9a8))')         KFNAME2(JF),(KFWBC(JF,JW),  JW=1,NWB)
    if(nwb < 10)write (CON,'(A8,(:9A8))')         KFNAME2(JF),(KFWBC(JF,JW),  JW=1,NWB)
    if(nwb >= 10)write (CON,'(A8,9A8,/(:8X,9A8))')         KFNAME2(JF),(KFWBC(JF,JW),  JW=1,NWB)          !cb 9/13/12  sw2/18/13  Foramt 6/16/13 8/13/13
  END DO
  write(con,'(/,"CST ICON   C2IWB   C2IWB   C2IWB   C2IWB   C2IWB   C2IWB   C2IWB   C2IWB   C2IWB")')
  DO JC=1,NCT
    write (CON,'(a8,9f8.3:/(:8x,9g8.3))')          cst_icon(jc),(C2I(JC,JW),    JW=1,NWB)
  END DO
  write(con,'(/,"CST PRIN  CPRWBC  CPRWBC  CPRWBC  CPRWBC  CPRWBC  CPRWBC  CPRWBC  CPRWBC  CPRWBC")')
  DO JC=1,NCT
    write (CON,'(10a8:/(:8x,9a8))')            cst_prin(jc),(CPRWBC(JC,JW), JW=1,NWB)
  END DO
  write(con,'(/,"CIN CON   CINBRC  CINBRC  CINBRC  CINBRC  CINBRC  CINBRC  CINBRC  CINBRC  CINBRC")')
  DO JC=1,NCT
    write (CON,'(10a8:/(:8x,9a8))')            cin_con(jc),(CINBRC(JC,JB), JB=1,NBR)
  END DO
  write(con,'(/,"CTR CON   CTRTRC  CTRTRC  CTRTRC  CTRTRC  CTRTRC  CTRTRC  CTRTRC  CTRTRC  CTRTRC")')
  DO JC=1,NCT
    write (CON,'(10a8:/(:8x,9a8))')            ctr_con(jc),(CTRTRC(JC,JT), JT=1,NTR)
  END DO
  write(con,'(/,"CDT CON   CDTBRC  CDTBRC  CDTBRC  CDTBRC  CDTBRC  CDTBRC  CDTBRC  CDTBRC  CDTBRC")')
  DO JC=1,NCT
    write (CON,'(10a8:/(:8x,9a8))')            cdt_con(jc),(CDTBRC(JC,JB), JB=1,NBR)
  END DO
  write(con,'(/,"CPR CON   CPRBRC  CPRBRC  CPRBRC  CPRBRC  CPRBRC  CPRBRC  CPRBRC  CPRBRC  CPRBRC")')
  DO JC=1,NCT
    write (CON,'(10a8:/(:8x,9a8))')            cpr_con(jc),(CPRBRC(JC,JB), JB=1,NBR)
  END DO

! Kinetics coefficients

  write(con,'(/,"EX COEF    EXH2O    EXSS    EXOM    BETA     EXC    EXIC")')
  write (CON,'(a8,4F8.5,2A8)')     (wb_name(jw),EXH2O(JW),  EXSS(JW),   EXOM(JW),   BETA(JW),   EXC(JW),   EXIC(JW),    JW=1,NWB)
  write(con,'(/,"ALG EX       EXA     EXA     EXA     EXA     EXA     EXA")')
  write (CON,'(8X,9F8.5)')         (EXA(JA),                                                                JA=1,NAL)
  write(con,'(/,"ZOO EX       EXZ     EXZ     EXZ     EXZ     EXZ     EXZ")')
  write (CON,'(8X,9F8.5)')         (EXZ(Jz),                                                                Jz=1,Nzpt)  !v3.5
  write(con,'(/,"MACRO EX     EXM     EXM     EXM     EXM     EXM     EXM")')
  write (CON,'(8X,9F8.5)')         (EXM(Jm),                                                                Jm=1,nmct)  !v3.5
  write(con,'(/,"GENERIC    CGQ10   CG0DK   CG1DK     CGS   CGLDK   CGKLF    CGCS")')
  !write (CON,'(a8,4F8.5)')         (gen_name(jg),CGQ10(JG),  CG0DK(JG),  CG1DK(JG),  CGS(JG),               JG=1,NGC)
   write (CON,'(a8,7F8.5)')         (gen_name(jg),CGQ10(JG),  CG0DK(JG),  CG1DK(JG),  CGS(JG), CGLDK(JG),CGKLF(JG),CGCS(JG),           JG=1,NGC) !LCJ 2/26/15
  write(con,'(/,"S SOLIDS     SSS   SEDRC   TAUCR")')
  write (CON,'(a8,F8.5,A,F8.5)')   (ss_name(js),SSS(JS),    SEDRC(JS),  TAUCR(JS),                       JS=1,NSS)
  write(con,'(/,"ALGAL RATE    AG      AR      AE      AM      AS    AHSP    AHSN   AHSSI    ASAT")')
  write (CON,'(a8,8F8.5,f8.2)')         (alg_name(ja),AG(JA),     AR(JA),     AE(JA),     AM(JA),     AS(JA),                            &
                                       AHSP(JA),   AHSN(JA),   AHSSI(JA),  ASAT(JA),                           JA=1,NAL)
  write(con,'(/,"ALGAL TEMP   AT1     AT2     AT3     AT4     AK1     AK2     AK3     AK4")')
  write (CON,'(a8,8F8.4)')         (alg_name(ja),AT1(JA),    AT2(JA),    AT3(JA),    AT4(JA),    AK1(JA),   AK2(JA),                         &
                                       AK3(JA),    AK4(JA),                                                    JA=1,NAL)
  write(con,'(/,"ALG STOI    ALGP    ALGN    ALGC   ALGSI   ACHLA   ALPOM   ANEQN    ANPR")')
  write (CON,'(a8,6F8.5,I8,F8.5)') (alg_name(ja),AP(JA),     AN(JA),     AC(JA),     ASI(JA),    ACHLA(JA), APOM(JA),                        &
                                       ANEQN(JA),  ANPR(JA),   JA=1,NAL)
  write(con,'(/,"EPIPHYTE    EPIC    EPIC    EPIC    EPIC    EPIC    EPIC    EPIC    EPIC    EPIC")')
  write (CON,'(10a8:/(:8x,9a8))')           epi_name(1),(EPIC(JW,1),                                                             JW=1,NWB)
  DO JE=2,NEPT
    write (CON,'(10a8:/(:8x,9a8))')             epi_name(je),(EPIC(JW,JE),                                                            JW=1,NWB)
  END DO
 write(con,'(/,"EPI PRIN    EPRC    EPRC    EPRC    EPRC    EPRC    EPRC    EPRC    EPRC    EPRC")')
  write (CON,'(10a8:/(:8x,9a8))')           epi_name(1),(EPIPRC(JW,1),                                                           JW=1,NWB)
  DO JE=2,NEPT
    write (CON,'(10a8:/(:8x,9a8))')             epi_name(je),(EPIPRC(JW,JE),                                                          JW=1,NWB)
  END DO
  write(con,'(/,"EPI INIT   EPICI   EPICI   EPICI   EPICI   EPICI   EPICI   EPICI   EPICI   EPICI")')
  write (CON,'(a8,9f8.4:/(:8x,9F8.4))')         epi_name(1),(EPICI(JW,1),                                                            JW=1,NWB)
  DO JE=2,NEPT
    write (CON,'(a8,9f8.4:/(:8x,9F8.4))')           epi_name(je),(EPICI(JW,JE),                                                           JW=1,NWB)
  END DO
  write(con,'(/,"EPI RATE      EG      ER      EE      EM      EB    EHSP    EHSN   EHSSI")')
  write (CON,'(a8,8F8.5)')         (epi_name(je),EG(JE),     ER(JE),     EE(JE),     EM(JE),     EB(JE),    EHSP(JE),                        &
                                       EHSN(JE),   EHSSI(JE),                                                  JE=1,NEP)
  write(con,'(/,"EPI HALF    ESAT     EHS   ENEQN    ENPR")')
  write (CON,'(a8,f8.2,F8.2,I8,F8.5)') (epi_name(je),ESAT(JE),   EHS(JE),    ENEQN(JE),  ENPR(JE),                           JE=1,NEP)
  write(con,'(/,"EPI TEMP     ET1     ET2     ET3     ET4     EK1     EK2     EK3     EK4")')
  write (CON,'(a8,8F8.4)')         (epi_name(je),ET1(JE),    ET2(JE),    ET3(JE),    ET4(JE),    EK1(JE),   EK2(JE),                         &
                                       EK3(JE),    EK4(JE),                                                    JE=1,NEP)
  write(con,'(/,"EPI STOI      EP      EN      EC     ESI   ECHLA    EPOM")')
  write (CON,'(a8,6F8.5)')         (epi_name(je),EP(JE),     EN(JE),     EC(JE),     ESI(JE),    ECHLA(JE), EPOM(JE),    JE=1,NEP)
! v3.5 start
  write(con,'(/,"ZOOP RATE     ZG      ZR      ZM    ZEFF   PREFP  ZOOMIN    ZS2P")')
  write (CON,'(a8,7F8.5)')         (zoo_name(jz),zg(jz),zr(jz),zm(jz),zeff(jz),PREFP(jz),ZOOMIN(jz),ZS2P(jz),            Jz=1,Nzpt)
  write(con,'(/,"ZOOP ALGP  PREFA   PREFA   PREFA   PREFA   PREFA   PREFA   PREFA   PREFA   PREFA")')
  write (CON,'(a8,8F8.5)')         zoo_name(1),(PREFA(ja,1),                                                            Ja=1,nal)          ! MM 7/13/06
  do jz=2,nzpt
    write (CON,'((a8,8F8.5))')       zoo_name(jz),(PREFA(ja,jz),                                                         Ja=1,nal)
  end do
  write(con,'(/,"ZOOP ZOOP  PREFZ   PREFZ   PREFZ   PREFZ   PREFZ   PREFZ   PREFZ   PREFZ   PREFZ")')
  write (CON,'(a8,8F8.5)')       zoo_name(1),(PREFz(jjz,1),                                                          Jjz=1,nzpt)
  do jz=2,nzpt
    write (CON,'((a8,8F8.5))')       zoo_name(jz),(PREFz(jjz,jz),                                                    Jjz=1,nzpt)           ! MM 7/13/06
  end do
  write(con,'(/,"ZOOP TEMP    ZT1     ZT2     ZT3     ZT4     ZK1     ZK2     ZK3     ZK4")')
  write (CON,'(a8,8F8.4)')         (zoo_name(jz),zT1(Jz),    zT2(Jz),    zT3(Jz),    zT4(Jz),    zK1(Jz),   zK2(Jz),                         &
                                       zK3(Jz),    zK4(Jz),                                                    Jz=1,Nzpt)
  write(con,'(/,"ZOOP STOI     ZP      ZN      ZC")')
  write (CON,'(a8,3F8.5)')         (zoo_name(jz),zP(Jz),     zN(Jz),     zC(Jz),                                         Jz=1,Nzpt)
  write(con,'(/,"MACROPHY  MACWBC  MACWBC  MACWBC  MACWBC  MACWBC  MACWBC  MACWBC  MACWBC  MACWBC")')
  write (CON,'(10a8:/(:8x,9a8))')           mac_name(1),(macwbC(JW,1),                                                           JW=1,NWB)
  DO Jm=2,nmct
    write (CON,'(10a8:/(:8x,9a8))')             mac_name(jm),(macwbC(JW,Jm),                                                          JW=1,NWB)
  END DO
  write(con,'(/,"MAC PRIN  MPRWBC  MPRWBC  MPRWBC  MPRWBC  MPRWBC  MPRWBC  MPRWBC  MPRWBC  MPRWBC")')
  write (CON,'(10a8:/(:8x,9a8))')           mac_name(1),(mprwbC(JW,1),                                                           JW=1,NWB)
  DO Jm=2,nmct
    write (CON,'(10a8:/(:8x,9a8))')             mac_name(jm),(mprwbC(JW,Jm),                                                          JW=1,NWB)
  END DO
  write(con,'(/,"MAC INI  MACWBCI MACWBCI MACWBCI MACWBCI MACWBCI MACWBCI MACWBCI MACWBCI MACWBCI")')
  write (CON,'(a8,9f8.4:/(:8x,9F8.4))')         mac_name(1),(macwbCI(JW,1),                                                          JW=1,NWB)
  DO Jm=2,nmct
    write (CON,'(a8,9f8.4:/(:8x,9F8.4))')           mac_name(jm),(macwbcI(JW,Jm),                                                         JW=1,NWB)
  END DO
  write(con,'(/,"MAC RATE      MG      MR      MM    MSAT    MHSP    MHSN    MHSC    MPOM  LRPMAC")')
  write (CON,'(a8,9F8.5)')         (mac_name(jm),mG(jm), mR(jm), mM(jm), msat(jm),mhsp(jm),mhsn(jm),mhsc(jm),                           &
                                          mpom(jm),lrpmac(jm),     jm=1,nmct)
  write(con,'(/,"MAC SED     PSED    NSED")')
  write (CON,'(a8,2F8.5)')         (mac_name(jm),psed(jm), nsed(jm),                                                     jm=1,nmct)
  write(con,'(/,"MAC DIST    MBMP    MMAX")')
  write (CON,'(a8,2F8.2)')         (mac_name(jm),mbmp(jm), mmax(jm),                                                     jm=1,nmct)
  write(con,'(/,"MAC DRAG  CDDRAG     DMV    DWSA   ANORM")')
  write (CON,'(a8,F8.5,g8.3,2f8.5)')         (mac_name(jm),cddrag(jm),dwv(jm),dwsa(jm),anorm(jm),                                 jm=1,nmct)  !cb 6/29/06
  write(con,'(/,"MAC TEMP     MT1     MT2     MT3     MT4     MK1     MK2     MK3     MK4")')
  write (CON,'(a8,8F8.4)')         (mac_name(jm),mT1(Jm),    mT2(Jm),    mT3(Jm),    mT4(Jm),    mK1(Jm),   mK2(Jm),                         &
                                       mK3(Jm),    mK4(Jm),                                                    Jm=1,nmct)
  write(con,'(/,"MAC STOICH    MP      MN      MC")')
  write (CON,'(a8,3F8.5)')         (mac_name(jm),mP(Jm),     mN(Jm),     mC(Jm),                                         Jm=1,nmct)
  write(con,'(/,"DOM       LDOMDK  RDOMDK   LRDDK")')
  write (CON,'(8X,3F8.5)')         (LDOMDK(JW), RDOMDK(JW), LRDDK(JW),                                      JW=1,NWB)
  write(con,'(/,"POM       LPOMDK  RPOMDK   LRPDK    POMS")')
  write (CON,'(8X,4F8.5)')         (LPOMDK(JW), RPOMDK(JW), LRPDK(JW),  POMS(JW),                           JW=1,NWB)
  write(con,'(/,"OM STOIC    ORGP    ORGN    ORGC   ORGSI")')
  write (CON,'(8X,4F8.5)')         (ORGP(JW),   ORGN(JW),   ORGC(JW),   ORGSI(JW),                          JW=1,NWB)
  write(con,'(/,"OM RATE     OMT1    OMT2    OMK1    OMK2")')
  write (CON,'(8X,4F8.5)')         (OMT1(JW),   OMT2(JW),   OMK1(JW),   OMK2(JW),                           JW=1,NWB)
  write(con,'(/,"CBOD        KBOD    TBOD    RBOD   CBODS")')
  write (CON,'(a8,4F8.5)')         (bod_name(jb),KBOD(JB),   TBOD(JB),   RBOD(JB), cbods(jb),                           JB=1,NBOD)  !v3.5
  write(con,'(/,"CBOD STOIC  BODP    BODN    BODC")')
  write (CON,'(a8,3F8.5)')         (bod_name(jb),BODP(JB),   BODN(JB),   BODC(JB),                                       JB=1,NBOD)
  write(con,'(/,"PHOSPHOR    PO4R   PARTP")')
  write (CON,'(a8,2F8.5)')         (wb_name(jw),PO4R(JW),   PARTP(JW),                                                  JW=1,NWB)
  write(con,'(/,"AMMONIUM    NH4R   NH4DK")')
  write (CON,'(a8,2F8.5)')         (wb_name(jw),NH4R(JW),   NH4DK(JW),                                                  JW=1,NWB)
  write(con,'(/,"NH4 RATE   NH4T1   NH4T2   NH4K1   NH4K2")')
  write (CON,'(a8,4F8.4)')         (wb_name(jw),NH4T1(JW),  NH4T2(JW),  NH4K1(JW),  NH4K2(JW),                          JW=1,NWB)
  write(con,'(/,"NITRATE    NO3DK    NO3S FNO3SED")')
  write (CON,'(a8,3F8.5)')         (wb_name(jw),NO3DK(JW),  NO3S(JW),   FNO3SED(JW),                                    JW=1,NWB)
  write(con,'(/,"NO3 RATE   NO3T1   NO3T2   NO3K1   NO3K2")')
  write (CON,'(a8,4F8.4)')         (wb_name(jw),NO3T1(JW),  NO3T2(JW),  NO3K1(JW),  NO3K2(JW),                          JW=1,NWB)
  write(con,'(/,"SILICA      DSIR    PSIS   PSIDK  PARTSI")')
  write (CON,'(a8,4F8.5)')         (wb_name(jw),DSIR(JW),   PSIS(JW),   PSIDK(JW),  PARTSI(JW),                         JW=1,NWB)
  write(con,'(/,"IRON         FER     FES")')
  write (CON,'(a8,2F8.5)')         (wb_name(jw),FER(JW),    FES(JW),                                                    JW=1,NWB)
  write(con,'(/,"SED CO2     CO2R")')
  write (CON,'(a8,F8.5)')          (wb_name(jw),CO2R(JW),                                                               JW=1,NWB)
  write(con,'(/,"STOICH 1   O2NH4    O2OM")')
  write (CON,'(a8,2F8.5)')         (wb_name(jw),O2NH4(JW),  O2OM(JW),                                                   JW=1,NWB)
  write(con,'(/,"STOICH 2    O2AR    O2AG")')
  write (CON,'(a8,2F8.5)')         (alg_name(ja),O2AR(JA),   O2AG(JA),                                                   JA=1,NAL)
  write(con,'(/,"STOICH 3    O2ER    O2EG")')
  write (CON,'(a8,2F8.5)')         (epi_name(je),O2ER(JE),   O2EG(JE),                                                   JE=1,NEPT)
  write(con,'(/,"STOICH 4    O2ZR")')
  write (CON,'(a8,F8.5)')          (zoo_name(jz),O2zR(Jz),                                                               Jz=1,Nzpt)
  write(con,'(/,"STOICH 5    O2MR    O2MG")')
  write (CON,'(a8,2F8.5)')         (mac_name(jm),O2mR(Jm),   O2mG(jm),                                                   Jm=1,nmct)
  write(con,'(/,"O2 LIMIT     KDO")')
  write (CON,'(8x,F8.5)')           KDO
  write(con,'(/,"SEDIMENT    SEDC  SEDPRC   SEDCI    SEDS    SEDK    FSOD    FSED   SEDBR DYNSEDK")')
  write (CON,'(a8,2A8,6F8.5,A8)')     (wb_name(jw),SEDCc(JW),   SEDPRC(JW), SEDCI(JW),  SDK(JW), seds(jw),   FSOD(JW),   FSED(JW), sedb(jw),DYNSEDK(JW),   JW=1,NWB)  ! cb 11/28/06
  write(con,'(/,"SOD RATE   SODT1   SODT2   SODK1   SODK2")')
  write (CON,'(a8,4F8.5)')         (wb_name(jw),SODT1(JW),  SODT2(JW),  SODK1(JW),  SODK2(JW),                          JW=1,NWB)
  write(con,'(/,"S DEMAND     SOD     SOD     SOD     SOD     SOD     SOD     SOD     SOD     SOD")')
  write (CON,'(8X,9F8.5)')         (SOD(I),                                                                  I=1,IMX)
  write(con,'(/,"REAERATION  TYPE    EQN#   COEF1   COEF2   COEF3   COEF4")')
  write (CON,'(a8,A8,I8,4F8.4)')   (wb_name(jw),REAERC(JW), NEQN(JW),   RCOEF1(JW), RCOEF2(JW), RCOEF3(JW), RCOEF4(JW), JW=1,NWB)

! Input filenames

  write(con,'(/,"RSI FILE..................................RSIFN.................................")')
  write (CON,'(8X,A72)')  RSIFN
  write(con,'(/,"QWD FILE..................................QWDFN.................................")')
  write (CON,'(8X,A72)')  QWDFN
  write(con,'(/,"QGT FILE..................................QGTFN.................................")')
  write (CON,'(8X,A72)')  QGTFN
  write(con,'(/,"WSC FILE..................................WSCFN.................................")')
  write (CON,'(8X,A72)')  WSCFN
  write(con,'(/,"SHD FILE..................................SHDFN.................................")')
  write (CON,'(8X,A72)')  SHDFN
  write(con,'(/,"BTH FILE..................................BTHFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),BTHFN(JW), JW=1,NWB)
  write(con,'(/,"MET FILE..................................METFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),METFN(JW), JW=1,NWB)
  write(con,'(/,"EXT FILE..................................EXTFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),EXTFN(JW), JW=1,NWB)
  write(con,'(/,"VPR FILE..................................VPRFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),VPRFN(JW), JW=1,NWB)
  write(con,'(/,"LPR FILE..................................LPRFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),LPRFN(JW), JW=1,NWB)
  write(con,'(/,"QIN FILE..................................QINFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),QINFN(JB), JB=1,NBR)
  write(con,'(/,"TIN FILE..................................TINFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),TINFN(JB), JB=1,NBR)
  write(con,'(/,"CIN FILE..................................CINFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),CINFN(JB), JB=1,NBR)
  write(con,'(/,"QOT FILE..................................QOTFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),QOTFN(JB), JB=1,NBR)
  write(con,'(/,"QTR FILE..................................QTRFN.................................")')
  write (CON,'(a8,A72)') (tr_name(jt),QTRFN(JT), JT=1,NTR)
  write(con,'(/,"TTR FILE..................................TTRFN.................................")')
  write (CON,'(a8,A72)') (tr_name(jt),TTRFN(JT), JT=1,NTR)
  write(con,'(/,"CTR FILE..................................CTRFN.................................")')
  write (CON,'(a8,A72)') (tr_name(jt),CTRFN(JT), JT=1,NTR)
  write(con,'(/,"QDT FILE..................................QDTFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),QDTFN(JB), JB=1,NBR)
  write(con,'(/,"TDT FILE..................................TDTFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),TDTFN(JB), JB=1,NBR)
  write(con,'(/,"CDT FILE..................................CDTFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),CDTFN(JB), JB=1,NBR)
  write(con,'(/,"PRE FILE..................................PREFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),PREFN(JB), JB=1,NBR)
  write(con,'(/,"TPR FILE..................................TPRFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),TPRFN(JB), JB=1,NBR)
  write(con,'(/,"CPR FILE..................................CPRFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),CPRFN(JB), JB=1,NBR)
  write(con,'(/,"EUH FILE..................................EUHFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),EUHFN(JB), JB=1,NBR)
  write(con,'(/,"TUH FILE..................................TUHFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),TUHFN(JB), JB=1,NBR)
  write(con,'(/,"CUH FILE..................................CUHFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),CUHFN(JB), JB=1,NBR)
  write(con,'(/,"EDH FILE..................................EDHFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),EDHFN(JB), JB=1,NBR)
  write(con,'(/,"TDH FILE..................................TDHFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),TDHFN(JB), JB=1,NBR)
  write(con,'(/,"CDH FILE..................................CDHFN.................................")')
  write (CON,'(a8,A72)') (br_name(jb),CDHFN(JB), JB=1,NBR)

! Output filenames

  write(con,'(/,"SNP FILE..................................SNPFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),SNPFN(JW), JW=1,NWB)
  write(con,'(/,"PRF FILE..................................PRFFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),PRFFN(JW), JW=1,NWB)
  write(con,'(/,"VPL FILE..................................VPLFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),VPLFN(JW), JW=1,NWB)
  write(con,'(/,"CPL FILE..................................CPLFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),CPLFN(JW), JW=1,NWB)
  write(con,'(/,"SPR FILE..................................SPRFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),SPRFN(JW), JW=1,NWB)
  write(con,'(/,"FLX FILE..................................FLXFN.................................")')
  write (CON,'(a8,A72)') (wb_name(jw),FLXFN(JW), JW=1,NWB)
  write(con,'(/,"TSR FILE..................................TSRFN.................................")')
  write (CON,'(8X,A72)')  TSRFN
  write(con,'(/,"WDO FILE..................................WDOFN.................................")')
  write (CON,'(8X,A72)')  WDOFN
  CLOSE (CON)
  
  ! Bathymetry file
  if(bthchng)then
   DO JW=1,NWB
    open(bth(jw),file=bthFNwrite(jw),status='unknown')
    
	                                   ! New Bathymetry format option SW 6/22/09
      IF(bthtype(jw)=='$')THEN
        write  (BTH(JW),'(a1)')bthtype(jw)
        write  (BTH(JW),*)
        write  (BTH(JW),*) AID,(DLX(I),  I=US(BS(JW))-1,DS(BE(JW))+1)
        write  (BTH(JW),*) AID,(ELWS(I), I=US(BS(JW))-1,DS(BE(JW))+1)
        write  (BTH(JW),*) AID,(PHI0(I), I=US(BS(JW))-1,DS(BE(JW))+1)
        write  (BTH(JW),*) AID,(FRIC(I), I=US(BS(JW))-1,DS(BE(JW))+1)
        write  (BTH(JW),*)
        DO K=1,KMX
          write  (BTH(JW),*) H(K,JW),(B(K,I),I=US(BS(JW))-1,DS(BE(JW))+1)
        END DO
      
      ELSE
       write(bth(jw),'(a)')bthline1(jw)
       write(bth(jw),'(a)')bthline2(jw)
       write(bth(jw),'("Segment lengths (DLX)")')
       write (BTH(JW),'(10F8.2)') (DLX(I),  I=US(BS(JW))-1,DS(BE(JW))+1)
       write(bth(jw),*)
       write(bth(jw),'("Water surface elevation [ELWS]")')
       write (BTH(JW),'(10F8.2)') (ELWS(I), I=US(BS(JW))-1,DS(BE(JW))+1)
       write(bth(jw),*)
       write(bth(jw),'("Segment orientation [PHI0]")')
       write (BTH(JW),'(10F8.2)') (PHI0(I), I=US(BS(JW))-1,DS(BE(JW))+1)
       write(bth(jw),*)
       write(bth(jw),'("Bottom friction [FRIC]")')
       write (BTH(JW),'(10F8.3)') (FRIC(I), I=US(BS(JW))-1,DS(BE(JW))+1)
       write(bth(jw),*)
       write(bth(jw),'("Layer heights, m [H]")')
       write (BTH(JW),'(10F8.3)') (H(K,JW), K=1,KMX)
       DO I=US(BS(JW))-1,DS(BE(JW))+1
         write(bth(Jw),*)
         write(bth(jw),'("Segment ",i4)')i
         write (BTH(JW),'(10F8.1)') (B(K,I), K=1,KMX)      
       END DO
	endif
    CLOSE (BTH(JW))
   END DO
  
  ! Water surface and bottom layers - need ktwb and kbmax to read vpr files

   DO JW=1,NWB
    DO JB=BS(JW),BE(JW)
      DO I=US(JB)-1,DS(JB)+1        
          KTI(I) = 2
          DO WHILE (EL(KTI(I),I) > ELWS(I))
            KTI(I) = KTI(I)+1
            if(kti(i) == kmx)then  ! cb 7/7/2010 if elws below grid, setting to elws to just within grid
              kti(i)=2
              elws(i)=el(kti(i),i)
              exit
            end if
          END DO
          Z(I)     = (EL(KTI(I),I)-ELWS(I))/COSA(JB)
          ZMIN(JW) = DMAX1(ZMIN(JW),Z(I))
          KTMAX    =  MAX(2,KTI(I))
          KTWB(JW) =  MAX(KTMAX,KTWB(JW))
          KTI(I)   =  MAX(KTI(I)-1,2)
          IF (Z(I) > ZMIN(JW)) IZMIN(JW) = I        
        K = 2
        DO WHILE (B(K,I) > 0.0)
          KB(I) = K
          K     = K+1
        END DO
        KBMAX(JW) = MAX(KBMAX(JW),KB(I))
      END DO
      KB(US(JB)-1) = KB(US(JB))
      KB(DS(JB)+1) = KB(DS(JB))
    END DO


!** Correct for water surface going over several layers

    
      KT = KTWB(JW)
      DO JB=BS(JW),BE(JW)
        DO I=US(JB)-1,DS(JB)+1
          H2(KT,I) = H(KT,JW)-Z(I)
          K        = KTI(I)+1
          DO WHILE (KT > K)
            Z(I)     = Z(I)-H(K,JW)
            H2(KT,I) = H(KT,JW)-Z(I)
            K        = K+1
          END DO
        END DO
      END DO    
    ELKT(JW) = EL(KTWB(JW),DS(BE(JW)))-Z(DS(BE(JW)))*COSA(BE(JW))
   END DO
  end if
  
  
  ! vertical profile files
  if(vprchng)then
  DO JW=1,NWB
    KT = KTWB(JW)
    IF (VERT_PROFILE(JW)) THEN

!**** Temperature and water quality

      OPEN (VPR(JW),FILE=vprfnwrite(jw),STATUS='unknown')      
 
      IF(vprtype(jw)=='$')THEN
         write( VPR(JW),'(a1)')vprtype(jw)
         write( VPR(JW),*)
         IF (VERT_TEMP(JW)) write (VPR(JW),*) IBLANK, (TVP(K,JW),K=KT,KBMAX(JW))
         IF (CONSTITUENTS) THEN
          DO JC=1,NCT
           IF (VERT_CONC(JC,JW))      write (VPR(JW),*) IBLANK, (CVP(K,JC,JW),  K=KT,KBMAX(JW))
          END DO
          DO JE=1,NEP
            IF (VERT_EPIPHYTON(JW,JE)) write (VPR(JW),*) IBLANK, (EPIVP(K,JW,JE),K=KT,KBMAX(JW))
          END DO
          IF (VERT_SEDIMENT(JW))       write (VPR(JW),*) IBLANK,(SEDVP(K,JW),   K=KT,KBMAX(JW))
          END IF
      ELSE
         write(vpr(jw),'(a)')vprline1
         !IF (VERT_TEMP(JW)) write (VPR(JW),'(/(8X,9F8.2))') (TVP(K,JW),K=KT,KBMAX(JW))
         IF (VERT_TEMP(JW))then
             write(vpr(jw),*)
             write(vpr(jw),'("TEMP         T2I     T2I     T2I     T2I     T2I     T2I     T2I     T2I     T2I")')
             write (VPR(JW),'((8X,9F8.2))') (TVP(K,JW),K=KT,KBMAX(JW))
         end if
         IF (CONSTITUENTS) THEN
           DO JC=1,NCT
            !IF (VERT_CONC(JC,JW))      write (VPR(JW),'(//(8X,9F8.3))') (CVP(K,JC,JW),  K=KT,KBMAX(JW))
            IF (VERT_CONC(JC,JW)) then
                write(vpr(jw),*)
                write(vpr(jw),'(a8,"     C2I     C2I     C2I     C2I     C2I     C2I     C2I     C2I     C2I")')cst_icon(jc)
                write (VPR(JW),'((8X,9F8.3))') (CVP(K,JC,JW),  K=KT,KBMAX(JW))
            end if
           END DO
           DO JE=1,NEP
            !IF (VERT_EPIPHYTON(JW,JE)) write (VPR(JW),'(//(8X,9F8.3))') (EPIVP(K,JW,JE),K=KT,KBMAX(JW))
              IF (VERT_EPIPHYTON(JW,JE)) then
                write(vpr(jw),*)
                write(vpr(jw),'(a8,"     EPI     EPI     EPI     EPI     EPI     EPI     EPI     EPI     EPI")')epi_name(je)
                write (VPR(JW),'((8X,9F8.3))') (EPIVP(K,JW,JE),K=KT,KBMAX(JW))
             end if
           END DO           
           !IF (VERT_SEDIMENT(JW))       write (VPR(JW),'(//(8X,9F8.3))') (SEDVP(K,JW),   K=KT,KBMAX(JW))
           IF (VERT_SEDIMENT(JW)) then
                write(vpr(jw),*)
                write(vpr(jw),'("     SED   SEDCI   SEDCI   SEDCI   SEDCI   SEDCI   SEDCI   SEDCI   SEDCI   SEDCI")')
                write (VPR(JW),'((8X,9F8.3))') (SEDVP(K,JW),   K=KT,KBMAX(JW))
             end if
          END IF
      ENDIF
      close(vpr(jw))
    END IF            
  end do
  end if
  
  
return
end

!**************************************************************
	subroutine changes

  USE MAIN
USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra; use selective1; use habitat

  integer cha, I_NUMBER
  real values(1000)
  character*200 tcard200
  character*2048 line1 ! 2048 is compiler limit
  character*8 tcard
  logical I_OPEN, I_EXIST
  real dum
  
  vertprf=.false.
  bthchng=.false.
  vprchng=.false.
  cha=9999      

  open(cha,file='changes.csv',status='old')

icnt=1  ! debug
10 read(cha,*,end=25)tcard200
   
   tcard=tcard200(1:8)

   if(tcard == 'MISCELL ')READ (cha,*,end=25)  blank, NOD,SELECTC,HABTATC,ENVIRPC,AERATEC,INITUWL,SYSTDG,N2BND,DOBND
   SELECTC=adjustr(SELECTC); HABTATC=adjustr(HABTATC); ENVIRPC=adjustr(ENVIRPC); AERATEC=adjustr(AERATEC); INITUWL=adjustr(INITUWL)
   SYSTDG=adjustr(SYSTDG); N2BND=adjustr(N2BND); DOBND=adjustr(DOBND)
   if(tcard == 'TIME CON')READ (cha,*,end=25)blank,  TMSTRT,   TMEND,    YEAR   
 ! restart parameters
   if(tcard == 'RESTART ')READ (cha,*,end=25)blank,         RSOC,    NRSO,    RSIC   
   RSOC=adjustr(RSOC); RSIC=adjustr(RSIC)
   if(tcard == 'RSO DATE')READ (cha,*,end=25)blank,        (RSOD(J), J=1,NRSO)   
   if(tcard == 'RSO FREQ')READ (cha,*,end=25)blank,        (RSOF(J), J=1,NRSO)
   if(tcard == 'RSI FILE')READ (cha,*,end=25)blank,           RSIFN
   rsifn=adjustl(rsifn)
 ! w2_selective.npt parameters
     if(tcard == 'OUT FREQ')read(cha,*,end=25)blank, tfrqtmp          
      w2select=.true.
      nxtstr=tmstrt      
     if(tcard == 'DYNSTR1 ')then
        
       w2select=.true.
       read(cha,*,end=25)blank,tempc,numtempc,tcdfreq          
       !write(*,*)'DYNSTR1 ' !debug
       tempc=adjustr(tempc)
        deAllocate (tcnelev,tcjb,tcjs, tcelev,tctemp,tctend,tctsrt,tciseg,tcklay,tcelevcon) 
        deAllocate (kstrsplt,tcyearly, jbmon,jsmon,tcntr) 
        deallocate (monctr)
        nxttcd=tmstrt
        nxtsplit=tmstrt
        nstt=numtempc
        Allocate (tcnelev(nstt),tcjb(nstt),tcjs(NSTt), tcelev(NSTt,11),tctemp(NSTt),tctend(NSTt),tctsrt(NSTt),tciseg(nstt),tcklay(nstt),tcelevcon(nstt)) 
        Allocate (kstrsplt(NSTt),tcyearly(NSTt), jbmon(nstt),jsmon(nstt),tcntr(nstt)) 
        allocate (monctr(nstt))             
      end if
      if(tcard == 'DYNSTR2 ')then
        w2select=.true.
        ncountc=0      
        do j=1,numtempc
          read(cha,*,end=25)blank,tcntr(j),tcjb(j),tcjs(j),tcyearly(j),tctsrt(j),tctend(j),tctemp(j),tcnelev(j),(tcelev(j,n),n=1,tcnelev(j))                    
          tcntr(j)=adjustr(tcntr(j)); tcyearly(j)=adjustr(tcyearly(j))
        end do
        !write(*,*)'DYNSTR2 ' !debug
      end if
      
      if(tcard == 'MONITOR ')then      
        w2select=.true.
        do j=1,numtempc
          read(cha,*,end=25)blank,tciseg(j),tcklay(j),DYNSEL(J)
          DYNSEL(J)=adjustr(DYNSEL(J))
        end do
        !write(*,*)'MONITOR ' !debug
      end if
      
      if(tcard == 'AUTO ELE')then
        w2select=.true.
        do j=1,numtempc
          read(cha,*,end=25)blank, tcelevcon(j)
          tcelevcon(j)=adjustr(tcelevcon(j))
        end do
        !write(*,*)'AUTO ELE' !debug
      end if
      
      if(tcard == 'SPLIT1  ')then        
        w2select=.true.
        deallocate(tspltcntr,tspltjb,tsyearly,tstsrt,tstend,tspltt,nouts,jstsplt, ELCONTSPL)
          read(cha,*,end=25)blank,tspltc,numtsplt        
        !  write(*,*)'SPLIT1  ' !debug
          tspltc=adjustr(tspltc)
        allocate(tspltcntr(numtsplt),tspltjb(numtsplt),tsyearly(numtsplt),tstsrt(numtsplt),tstend(numtsplt))
        allocate(tspltt(numtsplt),nouts(numtsplt),jstsplt(numtsplt,10),ELCONTSPL(NUMTSPLT))
      end if
      
      if(tcard == 'SPLIT2  ')then       
        w2select=.true.
        do j=1,numtsplt
           read(cha,*,end=25)blank,tspltcntr(j),tspltjb(j),tsyearly(j),tstsrt(j),tstend(j),tspltt(j),nouts(j),(jstsplt(j,n),n=1,nouts(j)),ELCONTSPL(J)     
           tspltcntr(j)=adjustr(tspltcntr(j)); tsyearly(j)=adjustr(tsyearly(j)); ELCONTSPL(J)=adjustr(ELCONTSPL(J))
        enddo        
       ! write(*,*)'SPLIT2  ' !debug
      end if
            
      if(tcard == 'THRESH1 ')then                  
        w2select=.true.
        deallocate(tempcrit,volmc)
        read(cha,*,end=25)blank, tempn
        allocate(tempcrit(nwb,tempn),volmc(nwb,tempn))
      end if
      if(tcard == 'THRESH2 ')then                                          
        w2select=.true.
        do j=1,tempn
          read(cha,*,end=25)blank,(tempcrit(jw,j),jw=1,nwb)   ! Note max of 10 waterbodies
        end do
      !  write(*,*)'THRESH2 ' !debug
      end if
   ! w2_habitat parameters
   if(tcard == '#FISH CR')then                  
     w2hab=.true.
     deallocate (fishname,fishtempl,fishtemph,fishdo,habvol,phabvol)                  
     read(cha,*,end=25)ifish,conhab
     allocate (fishname(ifish),fishtempl(ifish),fishtemph(ifish),fishdo(ifish),habvol(ifish),phabvol(ifish))
   end if
   if(tcard == 'NAMES OF')then          
     w2hab=.true.
     do i=1,ifish
       read(cha,*,end=25)fishname(i),fishtempl(i),fishtemph(i),fishdo(i)
     enddo  
     !write(*,*)'NAMES OF' !debug
   end if
   if(tcard == 'VOLUME W')then
     w2hab=.true.
     deallocate(isegvol,cdo,cpo4,cno3,cnh4,cchla,ctotp,cdos,cpo4s,cno3s,cnh4s,cchlas,ctotps,cgamma)                  
     read(cha,*,end=25)nseg,conavg
     !write(*,*)'VOLUME W' !debug
     allocate(isegvol(nseg),cdo(nseg),cpo4(nseg),cno3(nseg),cnh4(nseg),cchla(nseg),ctotp(nseg),cdos(nseg),cpo4s(nseg),cno3s(nseg),cnh4s(nseg),cchlas(nseg),ctotps(nseg),cgamma(nseg))
   end if
   if(tcard == 'SEGMENT ')then                  
     w2hab=.true.
     read(cha,*,end=25)(isegvol(i),i=1,nseg)
     !write(*,*)'SEGMENT ' !debug
   end if
   if(tcard == 'SURFACE ')then                  
     w2hab=.true.
     read(cha,*,end=25)kseg,consurf
     !write(*,*)'SURFACE ' !debug
   end if
   if(tcard == 'OutputFi')then                  
     w2hab=.true.
     read(cha,*,end=25)consod
     !write(*,*)'OutputFi' !debug
   end if
   
   ! water surface elevation
   if(tcard == 'ELWS WB ')then 
     bthchng=.true.
     read(cha,*)blank,jw     
     !write(*,*)'ELWS WB ' !debug
   end if
   
   if(bthchng)then
     if(tcard == 'ELWS SEG')then               
       READ (cha,*)dum,  (ELWS(I), I=US(BS(JW))-1,DS(BE(JW))+1)
       !write(*,*)'ELWS SEG' !debug
     end if
   end if
   
   ! initial conditions for temperature
   if(tcard == 'INIT CND')then      
     READ (cha,*)blank,     (T2I(JW), JW=1,NWB)
     !write(*,*)'NIT CND' !debug
     do jw=1,nwb
       if(t2i(jw)==-1.0)vertprf=.true.
     end do
   end if
   
   ! initial conditions for constituents
   if(tcard == 'CST ICON')then      
     DO JC=1,NCT
       READ (cha,*,end=25)blank,          (C2I(JC,JW),    JW=1,NWB)
     END DO
     !write(*,*)'CST ICON' !debug
     do jc=1,nct
       do jw=1,nwb
         if(c2i(jc,jw)==-1.0)vertprf=.true.
       end do
     end do
   end if
   
   ! vertical profiles if needed
   if(vertprf)then
     ! vertical profiles for temperature
     if(tcard == 'VPR TEMP')then                  
       vprchng=.true.
       read(cha,*)blank,jw 
       !write(*,*)'VPR TEMP1' !debug
       read(cha,*)
       !READ (cha,*,end=25)blank, (TVP(K,JW),K=KT,KBMAX(JW))                
       READ (cha,'(a)',end=25)line1
       values=0.0
       call line_parse(line1,values)
       ic=1
       do k=kt,kbmax(jw)
          tvp(k,jw)=values(ic)
          ic=ic+1
       end do
       
       !write(*,*)'VPR TEMP' !debug
     end if
     
     ! vertical profiles for constituents
     IF (CONSTITUENTS) THEN
       if(tcard == 'VPR CST ')then                  
         vprchng=.true.
         read(cha,*)blank,jw,jc
         read(cha,*)
         !READ (cha,*,end=25)blank, (CVP(K,JC,JW),  K=KT,KBMAX(JW))         
         
         READ (cha,'(a)',end=25)line1
         values=0.0
         call line_parse(line1,values)
         ic=1
         do k=kt,kbmax(jw)
            cvp(k,jc,jw)=values(ic)
            ic=ic+1
         end do
         
         !write(*,*)'VPR CST ' !debug
       end if
     end if
   end if          
   
   tcard200=blank
   icnt=icnt+1 !debug
go to 10

25 continue      
   
   DO JW=1,NWB    
    VERT_TEMP(JW)        = T2I(JW)     == -1        
    IF(CONSTITUENTS)THEN                     ! CB 12/04/08    
    VERT_SEDIMENT(JW)    = SEDCI(JW)   == -1.0 .AND. SEDCC(JW)   == '      ON'    
    VERT_EPIPHYTON(JW,:) = EPICI(JW,:) == -1.0 .AND. EPIC(JW,:) == '      ON'
    DO JC=1,NCT      
      VERT_CONC(JC,JW) = C2I(JC,JW) == -1.0 .AND. CAC(JC) == '      ON'      
      IF (VERT_CONC(JC,JW)) VERT_PROFILE(JW) = .TRUE.      
    END DO
    IF (VERT_SEDIMENT(JW))         VERT_PROFILE(JW) = .TRUE.    
    IF (ANY(VERT_EPIPHYTON(JW,:))) VERT_PROFILE(JW) = .TRUE.    
    END IF                          ! cb 12/04/08
    IF (VERT_TEMP(JW))             VERT_PROFILE(JW) = .TRUE.        
  END DO
   
  close(cha)

return
end

!*************************************************************
subroutine table_inflows_outflows

  USE MAIN
USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  
  integer tio, L4, L5, L6
  character*1 tmp1
  character*2 tmp2
  character*3 tmp3
  character*10 qcol(200)
  character*16 iounits(200),ioname(200),inorout(200)
  character*24 iotype(200)
  character*72 filename(200)
  real outfreq(200),freq
  
  
  tio=7777
  open(tio,file='table_inflows_outflows.csv',status='unknown')
  
!  write(tio,'("Name            Inflow/Outflow  Type                    Units           Q_Column  Filename                             Output_Timestep")')
  write(tio,186)  ! csv format
186 format('"Name","Inflow/Outflow","Type","Units","Q_Column","Filename","Output_Timestep"')
  
  icnt=0
  
! branch inflows  
  do jb=1,nbr
    if(uhs(jb)==0)then
      icnt=icnt+1
      if(jb < 10)then
        WRITE (tmp1,'(i1)') jb
        ioname(icnt)='qin_br'//tmp1
      else if( jb >= 10 .and. jb < 100)then
        WRITE (tmp2,'(i2)') jb
        ioname(icnt)='qin_br'//tmp2
      else if( jb >= 100)then
        WRITE (tmp3,'(i3)') jb
        ioname(icnt)='qin_br'//tmp3
      end if
      inorout(icnt)='inflow'      
      iotype(icnt)='upstream branch inflow'
      iounits(icnt)='m^3/s'
      filename(icnt)=qinfn(jb)    
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
    end if
  end do
  
! distributed tributaries
  do jb=1,nbr
    if(dtrc(jb)=='      ON')then
      icnt=icnt+1
      if(jb < 10)then
        WRITE (tmp1,'(i1)') jb
        ioname(icnt)='qdt_br'//tmp1
      else if( jb >= 10 .and. jb < 100)then
        WRITE (tmp2,'(i2)') jb
        ioname(icnt)='qdt_br'//tmp2
      else if( jb >= 100)then
        WRITE (tmp3,'(i3)') jb
        ioname(icnt)='qdt_br'//tmp3
      end if
      inorout(icnt)='inflow'      
      iotype(icnt)='distributed tributary'
      iounits(icnt)='m^3/s'
      filename(icnt)=qdtfn(jb)    
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
    end if
  end do
    
! tributaries  
  do jt=1,ntr    
      icnt=icnt+1
            
      WRITE (SEGNUM,'(I0)') itr(jt)
      SEGNUM = ADJUSTL(SEGNUM)
      L      = LEN_TRIM(SEGNUM)
      WRITE (SEGNUM2,'(I0)')jt
      SEGNUM2 = ADJUSTL(SEGNUM2)
      L2      = LEN_TRIM(SEGNUM2)
      ioname(icnt)  = 'qtr_'//SEGNUM2(1:L2)//'_seg'//SEGNUM(1:L)
      
      inorout(icnt)='inflow'      
      iotype(icnt)='tributary'
      iounits(icnt)='m^3/s'
      filename(icnt)=qtrfn(jt)      
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
  end do
  
  ! withdrawals
  do jw=1,nwd
    icnt=icnt+1      
      
      WRITE (SEGNUM,'(I0)') iwd(jw)
      SEGNUM = ADJUSTL(SEGNUM)
      L      = LEN_TRIM(SEGNUM)
      WRITE (SEGNUM2,'(I0)')jw
      SEGNUM2 = ADJUSTL(SEGNUM2)
      L2      = LEN_TRIM(SEGNUM2)
      ioname(icnt)  = 'wd_'//SEGNUM2(1:L2)//'_seg'//SEGNUM(1:L)
      
      WRITE (SEGNUM,'(I0)') nwd
      SEGNUM = ADJUSTL(SEGNUM)
      L      = LEN_TRIM(SEGNUM)
      WRITE (SEGNUM2,'(I0)')jw
      SEGNUM2 = ADJUSTL(SEGNUM2)
      L2      = LEN_TRIM(SEGNUM2)
      qcol(icnt)  = SEGNUM2(1:L2)//'/'//SEGNUM(1:L)
      
      inorout(icnt)='outflow'      
      iotype(icnt)='lateral withdrawal'
      iounits(icnt)='m^3/s'
      filename(icnt)=qwdfn
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
  end do

! precip
  DO JW=1,NWB
   if(prc(jw) == '      ON')then
     do jb=bs(jw),be(jw)
       icnt=icnt+1
      if(jb < 10)then
        WRITE (tmp1,'(i1)') jb
        ioname(icnt)='precip_br'//tmp1
      else if( jb >= 10 .and. jb < 100)then
        WRITE (tmp2,'(i2)') jb
        ioname(icnt)='precip_br'//tmp2
      else if( jb >= 100)then
        WRITE (tmp3,'(i3)') jb
        ioname(icnt)='precip_br'//tmp3
      end if
      inorout(icnt)='inflow'      
      iotype(icnt)='precipitation'
      iounits(icnt)='m/s'
      filename(icnt)=PREFN(JB)
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
     end do
   end if
 end do

 ! structural withdrawals
  do jb=1,nbr
  
    if(NSTR(JB) > 0)then
    
      do js=1,nstr(jb)
      
        icnt=icnt+1            
        WRITE (SEGNUM,'(I0)') js
        SEGNUM = ADJUSTL(SEGNUM)
        L      = LEN_TRIM(SEGNUM)
        WRITE (SEGNUM2,'(I0)')jb
        SEGNUM2 = ADJUSTL(SEGNUM2)
        L2      = LEN_TRIM(SEGNUM2)
        ioname(icnt)  = 'str_br'//SEGNUM2(1:L2)//'_'//SEGNUM(1:L)
                
        WRITE (SEGNUM,'(I0)') nstr(jb)
        SEGNUM = ADJUSTL(SEGNUM)
        L      = LEN_TRIM(SEGNUM)
        WRITE (SEGNUM2,'(I0)')js
        SEGNUM2 = ADJUSTL(SEGNUM2)
        L2      = LEN_TRIM(SEGNUM2)
        qcol(icnt)  = SEGNUM2(1:L2)//'/'//SEGNUM(1:L)
      
        inorout(icnt)='outflow'      
        iotype(icnt)='structural withdrawal'
        iounits(icnt)='m^3/s'
        filename(icnt)=QOTFN(JB)
        call Output_Timestep(filename(icnt),freq,1)
        outfreq(icnt)=freq
      end do  
    end if
  end do
  
  do ii=1,icnt
      
    ioname(ii)=ADJUSTL(ioname(ii))
    L1=LEN_TRIM(ioname(ii))
    inorout(ii)=ADJUSTL(inorout(ii))
    L2=LEN_TRIM(inorout(ii))
    iotype(ii)=ADJUSTL(iotype(ii))
    L3=LEN_TRIM(iotype(ii))
    iounits(ii)=ADJUSTL(iounits(ii))
    L4=LEN_TRIM(iounits(ii))
    qcol(ii)=ADJUSTL(qcol(ii))
    L5=LEN_TRIM(qcol(ii))
    filename(ii)=ADJUSTL(filename(ii))
    L6=LEN_TRIM(filename(ii))
      
    !write(tio,'(2a16,a24,a16,a10,a40,f12.6)')ioname(ii),inorout(ii),iotype(ii),iounits(ii),qcol(ii),filename(ii),outfreq(ii)
    write(tio,152)ioname(ii)(1:L1),inorout(ii)(1:L2),iotype(ii)(1:L3),iounits(ii)(1:L4),qcol(ii)(1:L5),filename(ii)(1:L6),outfreq(ii)   ! csv format
152   format('"'a,'","',a,'","',a,'","',a,'","',a,'","',a,'",',f12.6)
  end do
  
  close(tio)
  return
  end
  
!**************************************************************
	subroutine read_selective
	
	 USE MAIN
  USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use selective1
	
	open(1010,file='w2_selective.npt',status='old')
      do j=1,3
      read(1010,*)
      end do
      read(1010,'(8x,f8.0)')tfrqtmp
      nxtstr=tmstrt
      do j=1,2
      read(1010,*)
      end do
      read(1010,'(8x,a8,i8,f8.0)')tempc,numtempc,tcdfreq
      nxttcd=tmstrt
      nxtsplit=tmstrt
      nstt=numtempc
      
  Allocate (tcnelev(nstt),tcjb(nstt),tcjs(NSTt), tcelev(NSTt,11),tctemp(NSTt),tctend(NSTt),tctsrt(NSTt),ncountc(nst,nbr),tciseg(nstt),tcklay(nstt),tcelevcon(nstt)) 
  Allocate (tcyearly(NSTt), jbmon(nstt),jsmon(nstt),tcntr(nstt)) 
  allocate (volm(nwb),monctr(nstt),ncountcw(nwd),qwdfrac(nwd),qstrfrac(nst,nbr),DYNSEL(NSTT),SELD(NSTT),NXSEL(NSTT),TEMP2(NSTT))       
      
      do j=1,2
      read(1010,*)
      end do
      ncountc=0
      do j=1,numtempc
      read(1010,'(8x,a8,i8,i8,a8,f8.0,f8.0,f8.0,i8,10(f8.0))')tcntr(j),tcjb(j),tcjs(j),tcyearly(j),tctsrt(j),tctend(j),tctemp(j),tcnelev(j),(tcelev(j,n),n=1,tcnelev(j))
!        if(tcntr(j)=='      ST')then      
!        tcelev(j,tcnelev(j)+1)=ESTR(tcjs(j),tcjb(j))   ! always put the original elevation as the last elevation
!        else
!        tcelev(j,tcnelev(j)+1)=EWD(tcjs(j))   ! always put the original elevation as the last elevation
!        endif
      end do
      do j=1,2
      read(1010,*)
      end do
      do j=1,numtempc
      read(1010,'(8x,i8,f8.0,A8)')tciseg(j),tcklay(j),DYNSEL(J)      
      end do
      do j=1,2
      read(1010,*)
      end do
      do j=1,numtempc
      read(1010,'(8x,a8)')tcelevcon(j) 
      end do
      do j=1,2
      read(1010,*)
      end do
      read(1010,'(8x,a8,i8)')tspltc,numtsplt
      
      allocate(tsyearly(NUMTSPLT),tstsrt(NUMTSPLT),tstend(NUMTSPLT),tspltjb(NUMTSPLT),tspltt(NUMTSPLT),nouts(NUMTSPLT),jstsplt(NUMTSPLT,10),kstrsplt(NUMTSPLT),tspltcntr(NUMTSPLT), ELCONTSPL(NUMTSPLT))
      
      do j=1,2
      read(1010,*)
      end do
      do j=1,numtsplt
      read(1010,'(8X,A8,I8,A8,F8.0,F8.0,F8.0,I8,2I8,A8)')tspltcntr(j),tspltjb(j),tsyearly(j),tstsrt(j),tstend(j),tspltt(j),nouts(j),(jstsplt(j,n),n=1,nouts(j)),elcontspl(j)
      !if(nouts(j).gt.2)Write(*,*)'TCD NOUTS > 2 - only first 2 will be used'
      enddo
      do j=1,2
      read(1010,*)
      end do
      read(1010,'(8x,i8)')tempn
      do j=1,2
      read(1010,*)
      end do
      allocate(tempcrit(nwb,tempn),volmc(nwb,tempn))
      do j=1,tempn
        read(1010,'(8x,10f8.0)')(tempcrit(jw,j),jw=1,nwb)   ! Note max of 10 waterbodies
      end do
      close(1010)


      return
      end
!**************************************************************
	subroutine write_selective
	
	 USE MAIN
  USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use selective1;use extra
	
	open(1010,file=selfnwrite,status='unknown')
      
      write(1010,'("Selective input control file")')
      write(1010,'("Temperature outlet control - frequency of output for temperature")')
      write(1010,'("OUT FREQ TFRQTMP")')
      write(1010,'(8x,f8.5)')tfrqtmp
      nxtstr=tmstrt      
      write(1010,'("Structure outlet control based on time and temperature and branch")')
      write(1010,'("DYNSTR1  CONTROL    NUM    FREQ")')
      write(1010,'(8x,a8,i8,f8.3)')tempc,numtempc,tcdfreq
      nxttcd=tmstrt
      nxtsplit=tmstrt
      nstt=numtempc
                  
      write(1010,'(/,"DYNSTR2    ST/WD      JB   JS/NW  YEARLY    TSTR    TEND    TEMP   NELEV   ELEV1   ELEV2   ELEV3   ELEV4   ELEV5   ELEV6   ELEV7   ELEV8   ELEV9  ELEV10")')
      ncountc=0
      do j=1,numtempc
      write(1010,'(8x,a8,i8,i8,a8,f8.3,f8.3,f8.2,i8,10(f8.3))')tcntr(j),tcjb(j),tcjs(j),tcyearly(j),tctsrt(j),tctend(j),tctemp(j),tcnelev(j),(tcelev(j,n),n=1,tcnelev(j))        
      end do
      write(1010,'(/,"MONITOR LOC ISEG    ELEV  DYNSEL")')
      
      do j=1,numtempc
      write(1010,'(8x,i8,f8.2,a8)')tciseg(j),tcklay(j),DYNSEL(J)
      end do
      write(1010,'(/,"AUTO ELEVCONTROL")')      
      do j=1,numtempc
      write(1010,'(8x,a8)')tcelevcon(j) 
      end do
      write(1010,'(/,"SPLIT1      CNTR     NUM")')            
      write(1010,'(8x,a8,i8)')tspltc,numtsplt
      write(1010,'(/,"SPLIT2     ST/WD      JB  YEARLY    TSTR    TEND TTARGET   NOUTS JS1/NW1 JS2/NW2  ELCONT")')                  
      do j=1,numtsplt
      write(1010,'(8X,A8,I8,A8,F8.0,F8.0,F8.0,I8,2I8,A8)')tspltcntr(j),tspltjb(j),tsyearly(j),tstsrt(j),tstend(j),tspltt(j),nouts(j),(jstsplt(j,n),n=1,nouts(j)),ELCONTSPL(J)
      !if(nouts(j).gt.2)Write(*,*)'TCD NOUTS > 2 - only first 2 will be used'
      enddo
      write(1010,'(/," THRESH1   TEMPN")')                  
      
      write(1010,'(8x,i8)')tempn
      write(1010,'(/," THRESH2 TEMPCRI TEMPCRI")')                  
      
      !allocate(tempcrit(nwb,tempn),volmc(nwb,tempn))
      do j=1,tempn
        write(1010,'(8x,10f8.2)')(tempcrit(jw,j),jw=1,nwb)   ! Note max of 10 waterbodies
      end do
      close(1010)

      return
      end

!**************************************************************
subroutine read_fishhabitat

use global;use main;use screenc; use kinetic, only:o2, chla, no3,nh4,po4, tp, gamma, sed, sato; use tvdc, only: constituents; use namesc, only: cname2,cdname2; use logicc
use habitat

open(2500,file='w2_habitat.npt',status='old')
! skip 1st 2 lines
read(2500,'(a)')habline1
read(2500,'(a)')habline2
read(2500,*)ifish,conhab
allocate (fishname(ifish),fishtempl(ifish),fishtemph(ifish),fishdo(ifish),habvol(ifish),phabvol(ifish))
read(2500,'(a)')habline3
do i=1,ifish
  read(2500,*)fishname(i),fishtempl(i),fishtemph(i),fishdo(i)
enddo
read(2500,'(a)')habline4
read(2500,*)nseg,conavg     ! volume weighted averages of critical WQ parameters
read(2500,'(a)')habline5
allocate(isegvol(nseg),cdo(nseg),cpo4(nseg),cno3(nseg),cnh4(nseg),cchla(nseg),ctotp(nseg),cdos(nseg),cpo4s(nseg),cno3s(nseg),cnh4s(nseg),cchlas(nseg),ctotps(nseg),cgamma(nseg))
allocate(ssedd(imx))
read(2500,*)(isegvol(i),i=1,nseg)
read(2500,'(a)')habline6
read(2500,*)kseg,consurf     ! # of layers for surface average
read(2500,'(a)')habline7
read(2500,*)consod
close(2500)


return
end

!**************************************************************
subroutine write_fishhabitat

use global;use main;use screenc; use kinetic, only:o2, chla, no3,nh4,po4, tp, gamma, sed, sato; use tvdc, only: constituents; use namesc, only: cname2,cdname2; use logicc
use habitat; use extra

open(2500,file=habfnwrite,status='unknown')
! skip 1st 2 lines
write(2500,'(a)')habline1
write(2500,'(a)')habline2
write(2500,2210)ifish,conhab
2210 format(i8,',"',a,'",')
write(2500,'(a)')habline3
do i=1,ifish
  write(2500,2211)fishname(i),fishtempl(i),fishtemph(i),fishdo(i)
2211 format('"',a,'"',3(",",f8.3),",")
enddo
write(2500,'(a)')habline4
write(2500,2210)nseg,conavg     ! volume weighted averages of critical WQ parameters
write(2500,'(a)')habline5
write(2500,2212)(isegvol(i),i=1,nseg)
2212 format(<nseg>(i8,","))
write(2500,'(a)')habline6
write(2500,2210)kseg,consurf     ! # of layers for surface average
write(2500,'(a)')habline7
write(2500,2213)consod
2213 format('"',a,'",')
close(2500)


return
    end
    
!**************************************************************
	subroutine read_graph_npt_file

  USE MAIN
  USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  
  logical comma
  
  
  OPEN (GRF,FILE='graph.npt',STATUS='OLD')
  READ (GRF,'(///(A43,1X,A9,3F8.0,A8))') (HNAME(J),  FMTH(J),  HMULT(J),  HYMIN(J), HYMAX(J), HPLTC(J), J=1,NHY)
  READ (GRF,'(// (A43,1X,A9,3F8.0,A8))') (CNAME(J),  FMTC(J),  CMULT(J),  CMIN(J),  CMAX(J),  CPLTC(J), J=1,NCT)
  READ (GRF,'(// (A43,1X,A9,3F8.0,A8))') (CDNAME(J), FMTCD(J), CDMULT(J), CDMIN(J), CDMAX(J), CDPLTC(J),J=1,NDC)
  CLOSE (GRF)
  DO JC=1,NCT
    L3         = 1
    L1         = SCAN (CNAME(JC),',')+2
    cname4(jc) = cname(jc)(L3:L1-3)  ! for HEC-WAT plugin
      IF(L1 == 2)L1=43          ! SW 12/3/2012   Implies no comma found
    L2         = SCAN (CNAME(JC)(L1:43),'  ')+L1
      IF(L2 > 43)L2=43          ! SW 12/3/2012
    CUNIT(JC)  = CNAME(JC)(L1:L2)
    CNAME1(JC) = CNAME(JC)(1:L1-3)
    CNAME3(JC) = CNAME1(JC)
    DO WHILE (L3 < L1-3)
      IF (CNAME(JC)(L3:L3) == ' ') CNAME3(JC)(L3:L3) = '_'
      L3 = L3+1
    END DO
    CUNIT1(JC) = CUNIT(JC)(1:1)
    CUNIT2(JC) = CUNIT(JC)
    IF (CUNIT(JC)(1:2) == 'mg') THEN
      CUNIT1(JC) = 'g'
      CUNIT2(JC) = 'g/m^3'
    END IF
    IF (CUNIT(JC)(1:2) /= 'g/' .AND. CUNIT(JC)(1:2) /= 'mg') CUNIT1(JC) = '  '
  END DO
  DO JC=1,NDC
    comma=.false.
    L1          = 1
    !L2          = MAX(4,SCAN (CDNAME(JC),',')-1)        
    !CDNAME3(JC) = DNAME(JC)(1:L2)
    do L=1,43
     if(cdname(jc)(L:L)==',')then
       comma=.true.
       L3=L
       exit
     end if
    end do
    if(comma)then
      cdname3(jc)=cdname(jc)(1:L3-1)
    else
      do L=43,1,-1
        if(cdname(jc)(L:L)/=' ')then
           L4=L
           cdname3(jc)=cdname(jc)(1:L4)
           exit
        end if
      end do
    end if
    
    if(comma)then
      do L=43,L3+1,-1
        if(cdname(jc)(L:L)/=' ')then
           L5=L
           cdunit(jc)=cdname(jc)(L3+1:L5+1)
           exit
        end if
      end do
    else
      cdunit(jc)=' '
    end if
         
    
    !DO WHILE (L1 < L2)
    !  IF (CDNAME(JC)(L1:L1) == ' ') CDNAME3(JC)(L1:L1) = '_'
    !  L1 = L1+1
    !END DO
  END DO
  FMTH(1:NHY) = ADJUSTL (FMTH(1:NHY))

  return
    end
    
!*************************************************************
subroutine table_water_quality

  USE MAIN
USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  
  integer tio, L4, L5
  character*1 tmp1
  character*2 tmp2
  character*3 tmp3
  character*3 slashcol
  character*10 qcol(1000)
  character*16 iounits(1000),ioname(1000)
  character*43 wqparameter(1000)
  character*72 filename(1000)
  real outfreq(1000)
  
  
  tio=7777
  open(tio,file='table_water_quality.csv',status='unknown')
  
  !write(tio,'("Name            Parameter                                   Units        Data_Column  Filename                             Output_Timestep")')
  write(tio,186)  ! csv format
186 format('"Name","Parameter","Constituent_Name","Units","Data_Column","Filename","Output_Timestep"')
  
  icnt=0

! Temperature First
! branch inflows  
  do jb=1,nbr
    if(uhs(jb)==0)then
      icnt=icnt+1
      if(jb < 10)then
        WRITE (tmp1,'(i1)') jb
        ioname(icnt)='tin_br'//tmp1
      else if( jb >= 10 .and. jb < 100)then
        WRITE (tmp2,'(i2)') jb
        ioname(icnt)='tin_br'//tmp2
      else if( jb >= 100)then
        WRITE (tmp3,'(i3)') jb
        ioname(icnt)='tin_br'//tmp3
      end if
      wqparameter(icnt)='temperature'            
      iounits(icnt)='Celsius'
      filename(icnt)=tinfn(jb)    
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
    end if
  end do
  
! distributed tributaries
  do jb=1,nbr
    if(dtrc(jb)=='      ON')then
      icnt=icnt+1
      if(jb < 10)then
        WRITE (tmp1,'(i1)') jb
        ioname(icnt)='tdt_br'//tmp1
      else if( jb >= 10 .and. jb < 100)then
        WRITE (tmp2,'(i2)') jb
        ioname(icnt)='tdt_br'//tmp2
      else if( jb >= 100)then
        WRITE (tmp3,'(i3)') jb
        ioname(icnt)='tdt_br'//tmp3
      end if
      wqparameter(icnt)='temperature'            
      iounits(icnt)='Celsius'
      filename(icnt)=tdtfn(jb)    
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
    end if
  end do
    
! tributaries  
  do jt=1,ntr    
      icnt=icnt+1
            
      WRITE (SEGNUM,'(I0)') itr(jt)
      SEGNUM = ADJUSTL(SEGNUM)
      L      = LEN_TRIM(SEGNUM)
      WRITE (SEGNUM2,'(I0)')jt
      SEGNUM2 = ADJUSTL(SEGNUM2)
      L2      = LEN_TRIM(SEGNUM2)
      ioname(icnt)  = 'ttr_'//SEGNUM2(1:L2)//'_seg'//SEGNUM(1:L)
      
      wqparameter(icnt)='temperature'            
      iounits(icnt)='Celsius'
      filename(icnt)=ttrfn(jt)      
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
  end do
  
! precip  
  DO JW=1,NWB
   if(prc(jw) == '      ON')then
     do jb=bs(jw),be(jw)
       icnt=icnt+1
      if(jb < 10)then
        WRITE (tmp1,'(i1)') jb
        ioname(icnt)='tprecip_br'//tmp1
      else if( jb >= 10 .and. jb < 100)then
        WRITE (tmp2,'(i2)') jb
        ioname(icnt)='tprecip_br'//tmp2
      else if( jb >= 100)then
        WRITE (tmp3,'(i3)') jb
        ioname(icnt)='tprecip_br'//tmp3
      end if
      wqparameter(icnt)='temperature'            
      iounits(icnt)='Celsius'      
      filename(icnt)=TPRFN(JB)
      qcol(icnt)='1/1       '
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
     end do
   end if
 end do
 
  ! Constituents
if(constituents)then
! branch inflows  
  do jb=1,nbr
    if(uhs(jb)==0)then
! totaling number of values columns in file
      ncol=0
      do jc=1,nct
        if(CINBRC(JC,JB) == '      ON')then
          ncol=ncol+1
        end if
      end do
      if(ncol < 10)then
         WRITE (tmp1,'(i1)') ncol
         slashcol='/'//tmp1
      else if( ncol >= 10)then
         WRITE (tmp2,'(i2)') ncol
         slashcol='/'//tmp2          
      end if
      
      icol=0
      do jc=1,nct
        if(cinbrc(jc,jb)=='      ON')then  
          icol=icol+1
          icnt=icnt+1
          if(jb < 10)then
            WRITE (tmp1,'(i1)') jb
            ioname(icnt)='cin_br'//tmp1
          else if( jb >= 10 .and. jb < 100)then
            WRITE (tmp2,'(i2)') jb
            ioname(icnt)='cin_br'//tmp2
          else if( jb >= 100)then
            WRITE (tmp3,'(i3)') jb
            ioname(icnt)='cin_br'//tmp3
          end if          
          !wqparameter(icnt)=cname4(jc)
          wqparameter(icnt)=cname2(jc)   ! cb 12/7/17
          iounits(icnt)=cunit(jc)
          filename(icnt)=cinfn(jb)              
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol  
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol            
            qcol(icnt)=tmp2//slashcol
          end if
          call Output_Timestep(filename(icnt),freq,1)
          outfreq(icnt)=freq
        endif
      end do
    end if
  end do
  
  ! distributed tributaries
  do jb=1,nbr
    if(dtrc(jb)=='      ON')then
! totaling number of values columns in file
      ncol=0
      do jc=1,nct
        if(CDTBRC(JC,JB) == '      ON')then
          ncol=ncol+1
        end if
      end do
      if(ncol < 10)then
         WRITE (tmp1,'(i1)') ncol
         slashcol='/'//tmp1
      else if( ncol >= 10)then
         WRITE (tmp2,'(i2)') ncol
         slashcol='/'//tmp2          
      end if
      
      icol=0
      do jc=1,nct
        if(cdtbrc(jc,jb)=='      ON')then  
          icol=icol+1
          icnt=icnt+1
          if(jb < 10)then
            WRITE (tmp1,'(i1)') jb
            ioname(icnt)='cdt_br'//tmp1
          else if( jb >= 10 .and. jb < 100)then
            WRITE (tmp2,'(i2)') jb
            ioname(icnt)='cdt_br'//tmp2
          else if( jb >= 100)then
            WRITE (tmp3,'(i3)') jb
            ioname(icnt)='cdt_br'//tmp3
          end if          
          !wqparameter(icnt)=cname4(jc)            
          wqparameter(icnt)=cname2(jc)            ! cb 12/7/17
          iounits(icnt)=cunit(jc)
          filename(icnt)=cdtfn(jb)              
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol  
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol            
            qcol(icnt)=tmp2//slashcol
          end if
          call Output_Timestep(filename(icnt),freq,1)
          outfreq(icnt)=freq
        endif
      end do
    end if
  end do

  ! tributaries
  do jt=1,ntr    
! totaling number of values columns in file
      ncol=0
      do jc=1,nct
        if(CTRTRC(JC,JT) == '      ON')then
          ncol=ncol+1
        end if
      end do
      if(ncol < 10)then
         WRITE (tmp1,'(i1)') ncol
         slashcol='/'//tmp1
      else if( ncol >= 10)then
         WRITE (tmp2,'(i2)') ncol
         slashcol='/'//tmp2          
      end if
      
      icol=0
      do jc=1,nct
        if(CTRTRC(JC,JT)=='      ON')then  
          icol=icol+1
          icnt=icnt+1
          
          WRITE (SEGNUM,'(I0)') itr(jt)
          SEGNUM = ADJUSTL(SEGNUM)
          L      = LEN_TRIM(SEGNUM)
          WRITE (SEGNUM2,'(I0)')jt
          SEGNUM2 = ADJUSTL(SEGNUM2)
          L2      = LEN_TRIM(SEGNUM2)
          ioname(icnt)  = 'ctr_'//SEGNUM2(1:L2)//'_seg'//SEGNUM(1:L)
          
          !wqparameter(icnt)=cname4(jc)            
          wqparameter(icnt)=cname2(jc)            ! cb 12/7/17
          iounits(icnt)=cunit(jc)
          filename(icnt)=ctrfn(jt)              
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol  
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol            
            qcol(icnt)=tmp2//slashcol
          end if
          call Output_Timestep(filename(icnt),freq,1)
          outfreq(icnt)=freq
        endif
      end do    
  end do
  
  ! precip
  DO JW=1,NWB
   if(prc(jw) == '      ON')then
    do jb=bs(jw),be(jw)    
! totaling number of values columns in file
      ncol=0
      do jc=1,nct
        if(CPRBRC(JC,JB) == '      ON')then
          ncol=ncol+1
        end if
      end do
      if(ncol < 10)then
         WRITE (tmp1,'(i1)') ncol
         slashcol='/'//tmp1
      else if( ncol >= 10)then
         WRITE (tmp2,'(i2)') ncol
         slashcol='/'//tmp2          
      end if
      
      icol=0
      do jc=1,nct
        if(cprbrc(jc,jb)=='      ON')then  
          icol=icol+1
          icnt=icnt+1
          if(jb < 10)then
            WRITE (tmp1,'(i1)') jb
            ioname(icnt)='cprecip_br'//tmp1
          else if( jb >= 10 .and. jb < 100)then
            WRITE (tmp2,'(i2)') jb
            ioname(icnt)='cprecip_br'//tmp2
          else if( jb >= 100)then
            WRITE (tmp3,'(i3)') jb
            ioname(icnt)='cprecip_br'//tmp3
          end if          
          !wqparameter(icnt)=cname4(jc)            
          wqparameter(icnt)=cname2(jc)            ! cb 12/7/17
          iounits(icnt)=cunit(jc)
          filename(icnt)=cprfn(jb)              
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol  
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol            
            qcol(icnt)=tmp2//slashcol
          end if
          call Output_Timestep(filename(icnt),freq,1)
          outfreq(icnt)=freq
        endif
      end do    
    end do
   end if
  end do
  
end if
  
  
  do ii=1,icnt
    ioname(ii)=ADJUSTL(ioname(ii))
    L1=LEN_TRIM(ioname(ii))
    wqparameter(ii)=ADJUSTL(wqparameter(ii))
    L2=LEN_TRIM(wqparameter(ii))
    iounits(ii)=ADJUSTL(iounits(ii))
    L3=LEN_TRIM(iounits(ii))
    qcol(ii)=ADJUSTL(qcol(ii))
    L4=LEN_TRIM(qcol(ii))
    filename(ii)=ADJUSTL(filename(ii))
    L5=LEN_TRIM(filename(ii))
    !write(tio,'(a16,a43,a16,a10,a40,f12.6)')ioname(ii),wqparameter(ii),iounits(ii),qcol(ii),filename(ii),outfreq(ii)
    write(tio,152)ioname(ii)(1:L1),wqparameter(ii)(1:L2),iounits(ii)(1:L3),qcol(ii)(1:L4),filename(ii)(1:L5),outfreq(ii)   ! csv format
152   format('"'a,'","',a,'","',a,'","',a,'","',a,'",',f12.6)
  end do

  
  close(tio)
  return
    end

    
!*************************************************************
subroutine table_meteorology

  USE MAIN
USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  
  integer tio,  L4, L5
  character*1 iwb1
  character*2 iwb2
  character*3 iwb3
  character*10 qcol(100)
  character*16 iounits(100)
  character*16 metname(1000)
  character*43 wqparameter(100)
  character*72 filename(100)
  real outfreq(100)
  
  
  tio=7777
  open(tio,file='table_meteorology.csv',status='unknown')
  
  !write(tio,'("Name            Parameter                                   Units        Data_Column  Filename                             Output_Timestep")')
  write(tio,186)  ! csv format
186 format('"Name","Parameter","Units","Data_Column","Filename","Output_Timestep"')  
  
  icnt=0
  
  do jw=1,nwb
      icnt=icnt+1      
            
      if(jw.le.9)then    
	    write(iwb1,'(i1)')jw
        metname(icnt)='met_wb'//iwb1
	  end if
	  if(jw.ge.10.and.jw.le.99)then    
	    write(iwb2,'(i2)')jw
        metname(icnt)='met_wb'//iwb2
	  end if
	  if(jw.ge.100.and.jw.le.999)then    
	    write(iwb3,'(i3)')jw
        metname(icnt)='met_wb'//iwb3
	  end if  
            
      wqparameter(icnt)='air temperature'            
      iounits(icnt)='Celsius'
      filename(icnt)=metfn(jw)    
      if(SROC(JW) == '      ON')then
        qcol(icnt)='1/6       '    
      else
        qcol(icnt)='1/5       '    
      end if
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
      
      icnt=icnt+1      
      wqparameter(icnt)='dew point temperature'            
      iounits(icnt)='Celsius'
      filename(icnt)=metfn(jw)    
      if(SROC(JW) == '      ON')then
        qcol(icnt)='2/6       '    
      else
        qcol(icnt)='2/5       '    
      end if
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
      metname(icnt)=metname(icnt-1)
            
      
      icnt=icnt+1      
      wqparameter(icnt)='wind speed'            
      iounits(icnt)='m/s'
      filename(icnt)=metfn(jw)    
      if(SROC(JW) == '      ON')then
        qcol(icnt)='3/6       '    
      else
        qcol(icnt)='3/5       '    
      end if
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
      metname(icnt)=metname(icnt-1)
      
      icnt=icnt+1      
      wqparameter(icnt)='wind direction'            
      iounits(icnt)='radians'
      filename(icnt)=metfn(jw)    
      if(SROC(JW) == '      ON')then
        qcol(icnt)='4/6       '    
      else
        qcol(icnt)='4/5       '    
      end if
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
      metname(icnt)=metname(icnt-1)
      
      icnt=icnt+1      
      wqparameter(icnt)='cloud cover'            
      iounits(icnt)='0-10'
      filename(icnt)=metfn(jw)    
      if(SROC(JW) == '      ON')then
        qcol(icnt)='5/6       '    
      else
        qcol(icnt)='5/5       '    
      end if
      call Output_Timestep(filename(icnt),freq,1)
      outfreq(icnt)=freq
      metname(icnt)=metname(icnt-1)
      
        
      if(SROC(JW) == '      ON')then
        icnt=icnt+1      
        wqparameter(icnt)='short wave solar radiation'            
        iounits(icnt)='W/m^2'
        filename(icnt)=metfn(jw)          
        qcol(icnt)='6/6       '                
        call Output_Timestep(filename(icnt),freq,1)
        outfreq(icnt)=freq
        metname(icnt)=metname(icnt-1)
      end if
      
      
  end do
  
  do ii=1,icnt
      
    metname(ii)=ADJUSTL(metname(ii))
    L1=LEN_TRIM(metname(ii))
    wqparameter(ii)=ADJUSTL(wqparameter(ii))
    L2=LEN_TRIM(wqparameter(ii))
    iounits(ii)=ADJUSTL(iounits(ii))
    L3=LEN_TRIM(iounits(ii))
    qcol(ii)=ADJUSTL(qcol(ii))
    L4=LEN_TRIM(qcol(ii))
    filename(ii)=ADJUSTL(filename(ii))
    L5=LEN_TRIM(filename(ii))
    !write(tio,'(a16,a43,a16,a10,a40,f12.6)')metname(ii),wqparameter(ii),iounits(ii),qcol(ii),filename(ii),outfreq(ii)
      write(tio,152)metname(ii)(1:L1),wqparameter(ii)(1:L2),iounits(ii)(1:L3),qcol(ii)(1:L4),filename(ii)(1:L5),outfreq(ii)   ! csv format
152   format('"'a,'","',a,'","',a,'","',a,'","',a,'",',f12.6)
  end do
  
  close(tio)  
  return
    end
    
!*************************************************************
subroutine Output_Timestep(fnamefreq,freq,ifileflag)

  USE MAIN
USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  
  character*1 INFORMAT
  character*72 fnamefreq
  real freq,fbeg,fend
  integer linecount,ifile
  
  ifile=9912
  freq=-999.0
  linecount=0
  
  open(ifile,file=fnamefreq,status='old')
  
  if(ifileflag == 1)then
    READ(ifile,'(A1)')INFORMAT  
    read(ifile,*)
    read(ifile,*)
    IF(INFORMAT=='$')then
      read(ifile,*,end=120)fbeg
    else
      read(ifile,'(f8.0)',end=120)fbeg
    end if
    linecount=linecount+1
  else if(ifileflag==2)then  ! time series file
    do i=1,12
        read(ifile,*,end=120)
    end do      
    read(ifile,'(f10.0)',end=120)fbeg
    linecount=linecount+1
  end if
    
  
  IF(INFORMAT=='$')then
  
10  read(ifile,*,end=120)fend    
     linecount=linecount+1
   go to 10
   
  else
20  continue      
    if(ifileflag==1)then
      read(ifile,'(f8.0)',end=120)fend    
       linecount=linecount+1
    elseif(ifileflag==2)then
       read(ifile,'(f10.0)',end=120)fend    
       linecount=linecount+1
    end if
   go to 20
      
  end if
   
120 continue  
    
  freq=(fend-fbeg)/real(linecount-1)
  
  close(ifile)  
  return
    end

!*************************************************************
subroutine table_intial_water_surface_elevations

  USE MAIN
USE GLOBAL; USE GEOMC
  use extra
    
  
  tio=7777
  open(tio,file='table_initial_water_surface_elevation.csv',status='unknown')
  
  write(tio,186)  ! csv format
186 format('"Segment#","WSEL_m"')  
  
  
  
  do i=1,imx
    write(tio,152)i,elws(i)   ! csv format
152   format('"',i8,'","',f8.3,'"')
  end do
  
  close(tio)  
  return
    end


!*************************************************************
subroutine table_intial_concentrations

  USE MAIN
USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra
  
  integer tio, L4, L5    
  character*16 iounits(1000)
  character*43 wqparameter(1000)
  character*72 filename(1000)
  real valuei(1000)
  integer jwn(1000)
  
  
  tio=7777
  open(tio,file='table_initial_concentrations.csv',status='unknown')
  
  write(tio,186)  ! csv format
186 format('"Water_Body#","Parameter","Initial_Value","Units","Filename"')
  icnt=0
  
  filename='Not Used'
  valuei=-999.0
  
  do jw=1,nwb
    ! temperature first
    icnt=icnt+1      
    wqparameter(icnt)='temperature'            
    iounits(icnt)='Celsius'
    jwn(icnt)=jw
    if(t2i(jw) == -1.0)then
      filename(icnt)=vprfn(jw)    
    else
      valuei(icnt)=t2i(jw)
    end if
      
     !Constituents
    if(constituents)then                
       do jc=1,nct                  
          icnt=icnt+1          
          !wqparameter(icnt)=cname4(jc)
          wqparameter(icnt)=cname2(jc)  ! 12/7/17
          iounits(icnt)=cunit(jc)          
          jwn(icnt)=jw
          if(c2i(jc,jw) == -1.0)then
            filename(icnt)=vprfn(jw)    
          else
           valuei(icnt)=c2i(jc,jw)
          end if
        end do     
     end if    
  end do
  
  do ii=1,icnt    
    wqparameter(ii)=ADJUSTL(wqparameter(ii))
    L2=LEN_TRIM(wqparameter(ii))
    iounits(ii)=ADJUSTL(iounits(ii))
    L3=LEN_TRIM(iounits(ii))    
    filename(ii)=ADJUSTL(filename(ii))
    L5=LEN_TRIM(filename(ii))    
    write(tio,152)jwn(ii),wqparameter(ii)(1:L2),valuei(ii),iounits(ii)(1:L3),filename(ii)(1:L5)   ! csv format
152   format('"',i4,'","',a,'","',g12.5,'","',a,'","',a,'",')
  end do

  
  close(tio)
  return
    end

!*************************************************************
subroutine table_output_files

  USE MAIN
  USE GLOBAL;     USE NAMESC; USE GEOMC;  USE LOGICC; USE PREC;  USE SURFHE;  USE KINETIC; USE SHADEC; USE EDDY
  USE STRUCTURES; USE TRANS;  USE TVDC;   USE SELWC;  USE GDAYC; USE SCREENC; USE TDGAS;   USE RSTART; use KINETIC
  use macrophytec; use porosityc; use zooplanktonc  !v3.5
  use extra; use SELWC
  
  integer tio, L4, L5, L6, L7
  character*1 tmp1
  character*2 tmp2
  character*3 tmp3
  character*4 slashcol
  character*10 qcol(1000)
  character*16 iounits(1000)
  character*43 wqparameter(1000)
  character*72 filename(1000),laynum,depnum,vlocate,ioname(1000),sioname,outfn,isegn(1000)
  real outfreq(1000),freq
  CHARACTER(60) :: TITLEWITH2  
  CHARACTER(100) :: TITLEWITH  
  
  
  tio=7777
  open(tio,file='table_output_files.csv',status='unknown')
  
  !write(tio,'("Name            Parameter                                   Units        Data_Column  Filename                             Output_Timestep")')
  write(tio,186)  ! csv format
186 format('"Name","Segment","Parameter","Units","Data_Column","Filename","Output_Timestep"')
  
  icnt=0  
  

! ***  time series files    
    IF (TIME_SERIES) THEN

      L1 = SCAN(TSRFN,'.',BACK=.TRUE.)    ! SW 8/22/14
      DO J=1,NIKTSR
        icol=0
        WRITE (SEGNUM,'(I0)') ITSR(J)
        SEGNUM = ADJUSTL(SEGNUM)
        L      = LEN_TRIM(SEGNUM)
        WRITE (SEGNUM2,'(I0)')J
        SEGNUM2 = ADJUSTL(SEGNUM2)
        L2      = LEN_TRIM(SEGNUM2)
        TSRFN  = TSRFN(1:L1-1)//'_'//SEGNUM2(1:L2)//'_seg'//SEGNUM(1:L)//'.opt'
        freq=tsrf(1)

        I = ITSR(J)
        DO JW=1,NWB
          IF (I >= US(BS(JW)) .AND. I <= DS(BE(JW))) EXIT
        END DO


        ! totaling number of values columns in file
        ncol=0
        IF (ICE_COMPUTATION) THEN
          IF(SEDIMENT_CALC(JW))THEN
              ncol=22+nac+nep+nmc+nacd(Jw)+naf(jw)
              !ncol=15+nac+nep+nmc+nacd(Jw)+naf(jw)
          ELSE
              ncol=18+nac+nep+nmc+nacd(Jw)+naf(jw)
              !ncol=11+nac+nep+nmc+nacd(Jw)+naf(jw)
          END IF
        ELSE
          IF(SEDIMENT_CALC(JW))THEN
            ncol=21+nac+nep+nmc+nacd(Jw)+naf(jw)
            !  ncol=14+nac+nep+nmc+nacd(Jw)+naf(jw)
          ELSE
            ncol=17+nac+nep+nmc+nacd(Jw)+naf(jw)
            !  ncol=10+nac+nep+nmc+nacd(Jw)+naf(jw)
          END IF
        END IF
        if(ncol < 10)then
          WRITE (tmp1,'(i1)') ncol
          slashcol='/'//tmp1
        else if( ncol >= 10 .and. ncol <100)then
          WRITE (tmp2,'(i2)') ncol
          slashcol='/'//tmp2
        else if( ncol >= 100)then
          WRITE (tmp3,'(i3)') ncol
          slashcol='/'//tmp3
        end if

        IF (ETSR(J) < 0) THEN
          K = INT(ABS(ETSR(J)))
          WRITE (LAYNUM,'(I0)') K
          LAYNUM = ADJUSTL(LAYNUM)
          L6      = LEN_TRIM(LAYNUM)
          vlocate=' Layer '//LAYNUM(1:L6)
        ELSE
          WRITE (Depnum,'(f8.2)') etsr(j)
          depNUM = ADJUSTL(depNUM)
          L7      = LEN_TRIM(depNUM)
          vlocate=' Depth '//depNUM(1:L7)//' m'
        END IF

        L5=LEN_TRIM(vlocate)
        sioname='Time Series File '//SEGNUM2(1:L2)//' Segment '//SEGNUM(1:L)//vlocate(1:L5)


        ! DLT
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='DLT'
        iounits(icnt)='s'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq


        ! ELWS
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='ELWS'
        iounits(icnt)='m'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! T2
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='T2'
        iounits(icnt)='Celsius'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! U
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='U'
        iounits(icnt)='m/s'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! Q
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='Q'
        iounits(icnt)='m^3/s'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! SRON
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='SRON'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! EXT
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='EXT'
        iounits(icnt)='1/m'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! DEPTH
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='Depth'
        iounits(icnt)='m'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! WIDTH
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='WIDTH'
        iounits(icnt)='m'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! SHADE
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='SHADE'
        iounits(icnt)=' '
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        IF (ICE_COMPUTATION) THEN
          ! ICETH
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)='ICETH'
          iounits(icnt)='m'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq
        END if

        ! Tvolavg
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='Tvolavg'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! NetRad
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='NetRad'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! SWSolar
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='SWSolar'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! LWRad
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='LWRad'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! BackRad
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='BackRad'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! EvapF
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='EvapF'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! ConducF
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=tsrfn
        wqparameter(icnt)='ConducF'
        iounits(icnt)='W/m^2'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol
          qcol(icnt)=tmp2//slashcol
        end if
        outfreq(icnt)=freq

        ! constituents
        do jc=1,nac
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          !wqparameter(icnt)=CNAME4(CN(JC))
          wqparameter(icnt)=CNAME2(CN(JC))      ! cb 12/7/17
          iounits(icnt)=cunit(cn(jc))
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq
        end do

         ! epiphyton
        do je=1,nep
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          if(je < 10)then
            WRITE (tmp1,'(i1)') je
            wqparameter(icnt)='EPI'//tmp1
          else if( je >= 10)then
            WRITE (tmp2,'(i2)') je
            wqparameter(icnt)='EPI'//tmp2
          end if
          iounits(icnt)='g/m^3'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq
        end do

        ! macrophytes
        do je=1,nmc
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          if(je < 10)then
            WRITE (tmp1,'(i1)') je
            wqparameter(icnt)='MAC'//tmp1
          else if( je >= 10)then
            WRITE (tmp2,'(i2)') je
            wqparameter(icnt)='MAC'//tmp2
          end if
          iounits(icnt)='g/m^3'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq
        end do

        IF(SEDIMENT_CALC(JW))THEN
          ! SED
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)='SED'
          iounits(icnt)='g/m^3'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq

          ! SEDP
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)='SEDP'
          iounits(icnt)='g/m^3'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq

          ! SEDN
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)='SEDN'
          iounits(icnt)='g/m^3'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq

          ! SEDC
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)='SEDC'
          iounits(icnt)='g/m^3'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq

        END IF

        ! derived constituents
        do jd=1,nacd(jw)
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)=CDNAME3(cdn(jd,jw))
          iounits(icnt)=cdunit(cdn(jd,jw))
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq
        end do

        ! flux columns
        do jf=1,naf(jw)
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=tsrfn
          wqparameter(icnt)=KFNAME2(KFCN(JF,JW))
          iounits(icnt)='kg/d'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol
            qcol(icnt)=tmp2//slashcol
          end if
          outfreq(icnt)=freq
        end do
      end do

    end if
        
    
! withdrawal files
    
    IF (DOWNSTREAM_OUTFLOW) THEN
      freq=wdof(1)
      DO JWD=1,NIWDO
        icol=0
        WRITE (SEGNUM,'(I0)') IWDO(JWD)  
        SEGNUM = ADJUSTL(SEGNUM)  
        L      = LEN_TRIM(SEGNUM)
        ! flow file
        outfn='qwo_'//SEGNUM(1:L)//'.opt'
        sioname='Segment '//SEGNUM(1:L)//' Withdrawal flow'        
        ncol=1  
        DO Jb=1,nbr  
          IF (I >= US(jb) .AND. I <= DS(jb)) EXIT  
        END DO  
        do j=1,NSTR(JB)
           ncol=ncol+1
        end do
        if(ncol < 10)then
          WRITE (tmp1,'(i1)') ncol
          slashcol='/'//tmp1
        else if( ncol >= 10 .and. ncol <100)then
          WRITE (tmp2,'(i2)') ncol
          slashcol='/'//tmp2          
        else if( ncol >= 100)then
          WRITE (tmp3,'(i3)') ncol
          slashcol='/'//tmp3          
        end if
        
        icol = icol+1
        icnt = icnt+1
        ioname(icnt) = sioname
        isegn(icnt) = segnum(1:L)
        filename(icnt) = outfn
        wqparameter(icnt) = 'QWD'
        iounits(icnt) = 'm^3/s'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol  
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol            
          qcol(icnt)=tmp2//slashcol
        end if       
        outfreq(icnt)=freq
       
        do j=1,nstr(jb)
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=outfn
          wqparameter(icnt)='QWD'   
          iounits(icnt)='m^3/s'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol  
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol            
            qcol(icnt)=tmp2//slashcol
          end if       
          outfreq(icnt)=freq
        end do
        
        ! temperature file
        icol=0
        outfn='two_'//SEGNUM(1:L)//'.opt'
        sioname='Segment '//SEGNUM(1:L)//' Withdrawal Temperature'  
        ncol=1  
        DO Jb=1,nbr  
          IF (I >= US(jb) .AND. I <= DS(jb)) EXIT  
        END DO  
        do j=1,NSTR(JB)
           ncol=ncol+1
        end do
        if(ncol < 10)then
          WRITE (tmp1,'(i1)') ncol
          slashcol='/'//tmp1
        else if( ncol >= 10 .and. ncol <100)then
          WRITE (tmp2,'(i2)') ncol
          slashcol='/'//tmp2          
        else if( ncol >= 100)then
          WRITE (tmp3,'(i3)') ncol
          slashcol='/'//tmp3          
        end if
        
        icol=icol+1
        icnt=icnt+1
        ioname(icnt)=sioname
        isegn(icnt)=segnum(1:L)
        filename(icnt)=outfn
        wqparameter(icnt)='T'   
        iounits(icnt)='Celsius'
        if(icol < 10)then
          WRITE (tmp1,'(i1)') icol
          qcol(icnt)=tmp1//slashcol  
        else if( icol >= 10)then
          WRITE (tmp2,'(i2)') icol            
          qcol(icnt)=tmp2//slashcol
        end if       
        outfreq(icnt)=freq
       
        do j=1,nstr(jb)
          icol=icol+1
          icnt=icnt+1
          ioname(icnt)=sioname
          isegn(icnt)=segnum(1:L)
          filename(icnt)=outfn
          wqparameter(icnt)='T'   
          iounits(icnt)='Celsius'
          if(icol < 10)then
            WRITE (tmp1,'(i1)') icol
            qcol(icnt)=tmp1//slashcol  
          else if( icol >= 10)then
            WRITE (tmp2,'(i2)') icol            
            qcol(icnt)=tmp2//slashcol
          end if       
          outfreq(icnt)=freq
        end do
        
        
        ! constituent output        
        IF (CONSTITUENTS) THEN  
          icol=0
          outfn='cwo_'//SEGNUM(1:L)//'.opt'
          sioname='Segment '//SEGNUM(1:L)//' Withdrawal constituent'          
          ncol=0
           
          do jc=1,nac
             ncol=ncol+1
          end do
          if(ncol < 10)then
            WRITE (tmp1,'(i1)') ncol
            slashcol='/'//tmp1
          else if( ncol >= 10 .and. ncol <100)then
            WRITE (tmp2,'(i2)') ncol
            slashcol='/'//tmp2          
          else if( ncol >= 100)then
            WRITE (tmp3,'(i3)') ncol
            slashcol='/'//tmp3          
          end if
          do jc=1,nac
            icol=icol+1
            icnt=icnt+1
            ioname(icnt)=sioname
            isegn(icnt)=segnum(1:L)
            filename(icnt)=outfn
            !wqparameter(icnt)=CNAME4(CN(JC))
            wqparameter(icnt)=CNAME2(CN(JC))   ! cb 12/7/17
            iounits(icnt)=cunit(cn(jc))        
            if(icol < 10)then
              WRITE (tmp1,'(i1)') icol
              qcol(icnt)=tmp1//slashcol  
            else if( icol >= 10)then
              WRITE (tmp2,'(i2)') icol            
              qcol(icnt)=tmp2//slashcol
            end if       
            outfreq(icnt)=freq
          end do
        END IF  
        
        ! derived constituent output
        IF (DERIVED_CALC) THEN            
          icol=0
          outfn='dwo_'//SEGNUM(1:L)//'.opt'
          sioname='Segment '//SEGNUM(1:L)//' Withdrawal Derived Constituent'          
          ncol=0
          DO JW=1,NWB  
            IF (I >= US(BS(JW)) .AND. I <= DS(BE(JW))) EXIT  
          END DO  
          do jd=1,nacd(jw)
             ncol=ncol+1
          end do
          if(ncol < 10)then
            WRITE (tmp1,'(i1)') ncol
            slashcol='/'//tmp1
          else if( ncol >= 10 .and. ncol <100)then
            WRITE (tmp2,'(i2)') ncol
            slashcol='/'//tmp2          
          else if( ncol >= 100)then
            WRITE (tmp3,'(i3)') ncol
            slashcol='/'//tmp3          
          end if
          do jd=1,nacd(jw)
            icol=icol+1
            icnt=icnt+1
            ioname(icnt)=sioname
            isegn(icnt)=segnum(1:L)
            filename(icnt)=outfn
            wqparameter(icnt)=CDNAME3(cdn(jd,jw))
            iounits(icnt)=cdunit(cdn(jd,jw))
            if(icol < 10)then
              WRITE (tmp1,'(i1)') icol
              qcol(icnt)=tmp1//slashcol  
            else if( icol >= 10)then
              WRITE (tmp2,'(i2)') icol            
              qcol(icnt)=tmp2//slashcol
            end if       
            outfreq(icnt)=freq
          end do
        END IF  
      END DO  
    END IF
     
     
    IF (DOWNSTREAM_OUTFLOW) THEN  
      JFILE=0  
      DO JWD=1,NIWDO  
          
        ! Determine the # of withdrawals at the WITH SEG  
        DO JB=1,NBR  ! structures
          IF(IWDO(JWD)==DS(JB) .AND. NSTR(JB) /= 0)THEN  
            DO JS=1,NSTR(JB)                
              WRITE(SEGNUM2,'(I0)')IWDO(JWD)  
              SEGNUM2 = ADJUSTL(SEGNUM2)  
              L2      = LEN_TRIM(SEGNUM2)  
              TITLEWITH2=' STRUCTURAL WITHDRAWAL AT SEGMENT '//SEGNUM2(1:L2)  
              WRITE(SEGNUM,'(F10.0)')ESTR(JS,JB)  
              SEGNUM = ADJUSTL(SEGNUM)  
              L      = LEN_TRIM(SEGNUM)  
              TITLEWITH=ADJUSTL(TITLEWITH2)//' ELEV OF WITHDRAWAL CENTERLINE:'//SEGNUM(1:L)  
              WRITE(SEGNUM,'(I0)')JS  
              SEGNUM = ADJUSTL(SEGNUM)  
              L      = LEN_TRIM(SEGNUM)
              do LL=60,1,-1
                if(TITLEWITH2(LL:LL)/=' ')then
                  L4=LL          
                  exit
                end if
              end do
              
              ! flow              
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' flow'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='qwo_str'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='QWD'   
              iounits(icnt)='m^3/s'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
              ! temperature
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' temperature'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='two_str'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='T'   
              iounits(icnt)='Celsius'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
              ! constituents
              IF (CONSTITUENTS) THEN  
                 icol=0
                 outfn='cwo_str'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                 sioname=TITLEWITH2(1:L4)//' constituents'
                 ncol=0
           
                 do jc=1,nac
                   ncol=ncol+1
                 end do
                 if(ncol < 10)then
                   WRITE (tmp1,'(i1)') ncol
                   slashcol='/'//tmp1
                 else if( ncol >= 10 .and. ncol <100)then
                   WRITE (tmp2,'(i2)') ncol
                   slashcol='/'//tmp2          
                 else if( ncol >= 100)then
                   WRITE (tmp3,'(i3)') ncol
                   slashcol='/'//tmp3          
                 end if
                 do jc=1,nac
                   icol=icol+1
                   icnt=icnt+1
                   ioname(icnt)=sioname
                   isegn(icnt)=segnum2(1:L2)
                   filename(icnt)='cwo_str'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                   !wqparameter(icnt)=CNAME4(CN(JC))
                   wqparameter(icnt)=CNAME2(CN(JC))   ! cb 12/7/17
                   iounits(icnt)=cunit(cn(jc))        
                   if(icol < 10)then
                     WRITE (tmp1,'(i1)') icol
                     qcol(icnt)=tmp1//slashcol  
                   else if( icol >= 10)then
                     WRITE (tmp2,'(i2)') icol            
                     qcol(icnt)=tmp2//slashcol
                   end if       
                   outfreq(icnt)=freq
                 end do                                  
              ENDIF  
              ! derived constituents
              IF (DERIVED_CALC) THEN                                 
                DO JW=1,NWB  
                  IF (IWDO(JWD) >= US(BS(JW)) .AND. IWDO(JWD) <= DS(BE(JW))) EXIT  
                END DO  
                icol=0
                outfn='dwo_str'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                sioname=TITLEWITH2(1:L4)//' derived constituents'                
                ncol=0                  
                do jd=1,nacd(jw)
                  ncol=ncol+1
                end do
                if(ncol < 10)then
                  WRITE (tmp1,'(i1)') ncol
                  slashcol='/'//tmp1
                else if( ncol >= 10 .and. ncol <100)then
                  WRITE (tmp2,'(i2)') ncol
                  slashcol='/'//tmp2          
                else if( ncol >= 100)then
                  WRITE (tmp3,'(i3)') ncol
                  slashcol='/'//tmp3          
                end if
                do jd=1,nacd(jw)
                  icol=icol+1
                  icnt=icnt+1
                  ioname(icnt)=sioname
                  isegn(icnt)=segnum2(1:L2)
                  filename(icnt)=outfn
                  wqparameter(icnt)=CDNAME3(cdn(jd,jw))
                  iounits(icnt)=cdunit(cdn(jd,jw))
                  if(icol < 10)then
                    WRITE (tmp1,'(i1)') icol
                    qcol(icnt)=tmp1//slashcol  
                  else if( icol >= 10)then
                    WRITE (tmp2,'(i2)') icol            
                    qcol(icnt)=tmp2//slashcol
                  end if       
                  outfreq(icnt)=freq
                end do                
              ENDIF  
            ENDDO  
          ENDIF  
        ENDDO  
          
        DO JS=1,NWD  ! withdrawals
          IF(IWDO(JWD) == IWD(JS))THEN              
            WRITE(SEGNUM2,'(I0)')IWDO(JWD)  
            SEGNUM2 = ADJUSTL(SEGNUM2)  
            L2      = LEN_TRIM(SEGNUM2)  
            TITLEWITH2=' WITHDRAWAL AT SEG'//SEGNUM2(1:L2)  
            WRITE(SEGNUM,'(F10.0)')EWD(JS)  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            TITLEWITH=ADJUSTL(TITLEWITH2)//' ELEV OF WITHDRAWAL CENTERLINE:'//SEGNUM(1:L)  
            WRITE(SEGNUM,'(I0)')JS  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)             
            do LL=60,1,-1
              if(TITLEWITH2(LL:LL)/=' ')then
                L4=LL          
                exit
              end if
            end do
            
            ! flow              
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' flow'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='qwo_wd'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='QWD'   
              iounits(icnt)='m^3/s'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
              ! temperature
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' temperature'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='two_wd'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='T'   
              iounits(icnt)='Celsius'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
                        
            IF (CONSTITUENTS) THEN                                
                icol=0
                 outfn='cwo_wd'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                 sioname=TITLEWITH2(1:L4)//' constituents'
                 ncol=0           
                 do jc=1,nac
                   ncol=ncol+1
                 end do
                 if(ncol < 10)then
                   WRITE (tmp1,'(i1)') ncol
                   slashcol='/'//tmp1
                 else if( ncol >= 10 .and. ncol <100)then
                   WRITE (tmp2,'(i2)') ncol
                   slashcol='/'//tmp2          
                 else if( ncol >= 100)then
                   WRITE (tmp3,'(i3)') ncol
                   slashcol='/'//tmp3          
                 end if
                 do jc=1,nac
                   icol=icol+1
                   icnt=icnt+1
                   ioname(icnt)=sioname
                   isegn(icnt)=segnum2(1:L2)
                   filename(icnt)=outfn
                   !wqparameter(icnt)=CNAME4(CN(JC))
                   wqparameter(icnt)=CNAME2(CN(JC))   ! cb 12/7/17
                   iounits(icnt)=cunit(cn(jc))        
                   if(icol < 10)then
                     WRITE (tmp1,'(i1)') icol
                     qcol(icnt)=tmp1//slashcol  
                   else if( icol >= 10)then
                     WRITE (tmp2,'(i2)') icol            
                     qcol(icnt)=tmp2//slashcol
                   end if       
                   outfreq(icnt)=freq
                 end do
            ENDIF  
            IF (DERIVED_CALC) THEN                
                DO JW=1,NWB  
                  IF (IWDO(JWD) >= US(BS(JW)) .AND. IWDO(JWD) <= DS(BE(JW))) EXIT  
                END DO  
                icol=0
                outfn='dwo_wd'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                sioname=TITLEWITH2(1:L4)//' derived constituents'                
                ncol=0                  
                do jd=1,nacd(jw)
                  ncol=ncol+1
                end do
                if(ncol < 10)then
                  WRITE (tmp1,'(i1)') ncol
                  slashcol='/'//tmp1
                else if( ncol >= 10 .and. ncol <100)then
                  WRITE (tmp2,'(i2)') ncol
                  slashcol='/'//tmp2          
                else if( ncol >= 100)then
                  WRITE (tmp3,'(i3)') ncol
                  slashcol='/'//tmp3          
                end if
                do jd=1,nacd(jw)
                  icol=icol+1
                  icnt=icnt+1
                  ioname(icnt)=sioname
                  isegn(icnt)=segnum2(1:L2)
                  filename(icnt)=outfn
                  wqparameter(icnt)=CDNAME3(cdn(jd,jw))
                  iounits(icnt)=cdunit(cdn(jd,jw))
                  if(icol < 10)then
                    WRITE (tmp1,'(i1)') icol
                    qcol(icnt)=tmp1//slashcol  
                  else if( icol >= 10)then
                    WRITE (tmp2,'(i2)') icol            
                    qcol(icnt)=tmp2//slashcol
                  end if       
                  outfreq(icnt)=freq
                end do
            ENDIF  
          ENDIF  
        ENDDO  
          
        DO JS=1,NSP  ! spillways
          IF(IWDO(JWD) == IUSP(JS))THEN  
             
            WRITE(SEGNUM2,'(I0)')IWDO(JWD)  
            SEGNUM2 = ADJUSTL(SEGNUM2)  
            L2      = LEN_TRIM(SEGNUM2)  
            TITLEWITH2=' SPILLWAY WITHDRAWAL AT SEG'//SEGNUM2(1:L2)  
            WRITE(SEGNUM,'(F10.0)')ESP(JS)  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            TITLEWITH=ADJUSTL(TITLEWITH2)//' ELEV OF WITHDRAWAL CENTERLINE:'//SEGNUM(1:L)  
            WRITE(SEGNUM,'(I0)')JS  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            do LL=60,1,-1
              if(TITLEWITH2(LL:LL)/=' ')then
                L4=LL          
                exit
              end if
            end do
            
            ! flow              
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' flow'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='qwo_sp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='QWD'   
              iounits(icnt)='m^3/s'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
              ! temperature
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' temperature'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='two_sp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='T'   
              iounits(icnt)='Celsius'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq            
            
            IF (CONSTITUENTS) THEN                              
                 icol=0
                 outfn='cwo_sp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                 sioname=TITLEWITH2(1:L4)//' constituents'
                 ncol=0           
                 do jc=1,nac
                   ncol=ncol+1
                 end do
                 if(ncol < 10)then
                   WRITE (tmp1,'(i1)') ncol
                   slashcol='/'//tmp1
                 else if( ncol >= 10 .and. ncol <100)then
                   WRITE (tmp2,'(i2)') ncol
                   slashcol='/'//tmp2          
                 else if( ncol >= 100)then
                   WRITE (tmp3,'(i3)') ncol
                   slashcol='/'//tmp3          
                 end if
                 do jc=1,nac
                   icol=icol+1
                   icnt=icnt+1
                   ioname(icnt)=sioname
                   isegn(icnt)=segnum2(1:L2)
                   filename(icnt)=outfn
                   !wqparameter(icnt)=CNAME4(CN(JC))
                   wqparameter(icnt)=CNAME2(CN(JC))
                   iounits(icnt)=cunit(cn(jc))        
                   if(icol < 10)then
                     WRITE (tmp1,'(i1)') icol
                     qcol(icnt)=tmp1//slashcol  
                   else if( icol >= 10)then
                     WRITE (tmp2,'(i2)') icol            
                     qcol(icnt)=tmp2//slashcol
                   end if       
                   outfreq(icnt)=freq
                 end do
            ENDIF  
            IF (DERIVED_CALC) THEN                
                DO JW=1,NWB  
                  IF (IWDO(JWD) >= US(BS(JW)) .AND. IWDO(JWD) <= DS(BE(JW))) EXIT  
                END DO  
                icol=0
                outfn='dwo_sp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                sioname=TITLEWITH2(1:L4)//' derived constituents'                
                ncol=0                  
                do jd=1,nacd(jw)
                  ncol=ncol+1
                end do
                if(ncol < 10)then
                  WRITE (tmp1,'(i1)') ncol
                  slashcol='/'//tmp1
                else if( ncol >= 10 .and. ncol <100)then
                  WRITE (tmp2,'(i2)') ncol
                  slashcol='/'//tmp2          
                else if( ncol >= 100)then
                  WRITE (tmp3,'(i3)') ncol
                  slashcol='/'//tmp3          
                end if
                do jd=1,nacd(jw)
                  icol=icol+1
                  icnt=icnt+1
                  ioname(icnt)=sioname
                  isegn(icnt)=segnum2(1:L2)
                  filename(icnt)=outfn
                  wqparameter(icnt)=CDNAME3(cdn(jd,jw))
                  iounits(icnt)=cdunit(cdn(jd,jw))
                  if(icol < 10)then
                    WRITE (tmp1,'(i1)') icol
                    qcol(icnt)=tmp1//slashcol  
                  else if( icol >= 10)then
                    WRITE (tmp2,'(i2)') icol            
                    qcol(icnt)=tmp2//slashcol
                  end if       
                  outfreq(icnt)=freq
                end do
            ENDIF  
          ENDIF  
        ENDDO  
          
          
        DO JS=1,NPU  ! pumps
          IF(IWDO(JWD) == IUPU(JS))THEN                
            WRITE(SEGNUM2,'(I0)')IWDO(JWD)  
            SEGNUM2 = ADJUSTL(SEGNUM2)  
            L2      = LEN_TRIM(SEGNUM2)  
            TITLEWITH2='PUMP WITHDRAWAL AT SEG'//SEGNUM2(1:L2)  
            WRITE(SEGNUM,'(F10.0)')EPU(JS)  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            TITLEWITH=ADJUSTL(TITLEWITH2)//' ELEV OF WITHDRAWAL CENTERLINE:'//SEGNUM(1:L)  
            WRITE(SEGNUM,'(I0)')JS  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            do LL=60,1,-1
              if(TITLEWITH2(LL:LL)/=' ')then
                L4=LL          
                exit
              end if
            end do
                        
            ! flow              
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' flow'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='qwo_pmp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='QWD'   
              iounits(icnt)='m^3/s'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
              ! temperature
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' temperature'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='two_pmp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='T'   
              iounits(icnt)='Celsius'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq            
            
            
            IF (CONSTITUENTS) THEN                              
                 icol=0
                 outfn='cwo_pmp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                 sioname=TITLEWITH2(1:L4)//' constituents'
                 ncol=0           
                 do jc=1,nac
                   ncol=ncol+1
                 end do
                 if(ncol < 10)then
                   WRITE (tmp1,'(i1)') ncol
                   slashcol='/'//tmp1
                 else if( ncol >= 10 .and. ncol <100)then
                   WRITE (tmp2,'(i2)') ncol
                   slashcol='/'//tmp2          
                 else if( ncol >= 100)then
                   WRITE (tmp3,'(i3)') ncol
                   slashcol='/'//tmp3          
                 end if
                 do jc=1,nac
                   icol=icol+1
                   icnt=icnt+1
                   ioname(icnt)=sioname
                   isegn(icnt)=segnum2(1:L2)
                   filename(icnt)=outfn
                   !wqparameter(icnt)=CNAME4(CN(JC))
                   wqparameter(icnt)=CNAME2(CN(JC))   ! cb 12/7/17
                   iounits(icnt)=cunit(cn(jc))        
                   if(icol < 10)then
                     WRITE (tmp1,'(i1)') icol
                     qcol(icnt)=tmp1//slashcol  
                   else if( icol >= 10)then
                     WRITE (tmp2,'(i2)') icol            
                     qcol(icnt)=tmp2//slashcol
                   end if       
                   outfreq(icnt)=freq
                 end do
              
            ENDIF  
            IF (DERIVED_CALC) THEN                              
                DO JW=1,NWB  
                  IF (IWDO(JWD) >= US(BS(JW)) .AND. IWDO(JWD) <= DS(BE(JW))) EXIT  
                END DO  
                icol=0
                outfn='dwo_pmp'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                sioname=TITLEWITH2(1:L4)//' derived constituents'                
                ncol=0                  
                do jd=1,nacd(jw)
                  ncol=ncol+1
                end do
                if(ncol < 10)then
                  WRITE (tmp1,'(i1)') ncol
                  slashcol='/'//tmp1
                else if( ncol >= 10 .and. ncol <100)then
                  WRITE (tmp2,'(i2)') ncol
                  slashcol='/'//tmp2          
                else if( ncol >= 100)then
                  WRITE (tmp3,'(i3)') ncol
                  slashcol='/'//tmp3          
                end if
                do jd=1,nacd(jw)
                  icol=icol+1
                  icnt=icnt+1
                  ioname(icnt)=sioname
                  isegn(icnt)=segnum2(1:L2)
                  filename(icnt)=outfn
                  wqparameter(icnt)=CDNAME3(cdn(jd,jw))
                  iounits(icnt)=cdunit(cdn(jd,jw))
                  if(icol < 10)then
                    WRITE (tmp1,'(i1)') icol
                    qcol(icnt)=tmp1//slashcol  
                  else if( icol >= 10)then
                    WRITE (tmp2,'(i2)') icol            
                    qcol(icnt)=tmp2//slashcol
                  end if       
                  outfreq(icnt)=freq
                end do
            ENDIF  
          ENDIF  
        ENDDO  
          
          
        DO JS=1,NPI  ! pipes
          IF(IWDO(JWD) == IUPI(JS))THEN  
            JFILE=JFILE+1  
            WRITE(SEGNUM2,'(I0)')IWDO(JWD)  
            SEGNUM2 = ADJUSTL(SEGNUM2)  
            L2      = LEN_TRIM(SEGNUM2)  
            TITLEWITH2='PIPE WITHDRAWAL AT SEG'//SEGNUM2(1:L2)  
            WRITE(SEGNUM,'(F10.0)')EUPI(JS)  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            TITLEWITH=ADJUSTL(TITLEWITH2)//' ELEV OF WITHDRAWAL CENTERLINE:'//SEGNUM(1:L)  
            WRITE(SEGNUM,'(I0)')JS  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            do LL=60,1,-1
              if(TITLEWITH2(LL:LL)/=' ')then
                L4=LL          
                exit
              end if
            end do
            
            ! flow              
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' flow'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='qwo_pipe'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='QWD'   
              iounits(icnt)='m^3/s'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
             ! temperature
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' temperature'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='two_pipe'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='T'   
              iounits(icnt)='Celsius'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq            
                                    
            IF (CONSTITUENTS) THEN                
                 icol=0
                 outfn='cwo_pipe'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                 sioname=TITLEWITH2(1:L4)//' constituents'
                 ncol=0           
                 do jc=1,nac
                   ncol=ncol+1
                 end do
                 if(ncol < 10)then
                   WRITE (tmp1,'(i1)') ncol
                   slashcol='/'//tmp1
                 else if( ncol >= 10 .and. ncol <100)then
                   WRITE (tmp2,'(i2)') ncol
                   slashcol='/'//tmp2          
                 else if( ncol >= 100)then
                   WRITE (tmp3,'(i3)') ncol
                   slashcol='/'//tmp3          
                 end if
                 do jc=1,nac
                   icol=icol+1
                   icnt=icnt+1
                   ioname(icnt)=sioname
                   isegn(icnt)=segnum2(1:L2)
                   filename(icnt)=outfn
                   !wqparameter(icnt)=CNAME4(CN(JC))
                   wqparameter(icnt)=CNAME2(CN(JC))  ! cb 12/7/17
                   iounits(icnt)=cunit(cn(jc))        
                   if(icol < 10)then
                     WRITE (tmp1,'(i1)') icol
                     qcol(icnt)=tmp1//slashcol  
                   else if( icol >= 10)then
                     WRITE (tmp2,'(i2)') icol            
                     qcol(icnt)=tmp2//slashcol
                   end if       
                   outfreq(icnt)=freq
                 end do              
            ENDIF  
            IF (DERIVED_CALC) THEN  
              
                DO JW=1,NWB  
                  IF (IWDO(JWD) >= US(BS(JW)) .AND. IWDO(JWD) <= DS(BE(JW))) EXIT  
                END DO  
                icol=0
                outfn='dwo_pipe'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                sioname=TITLEWITH2(1:L4)//' derived constituents'                
                ncol=0                  
                do jd=1,nacd(jw)
                  ncol=ncol+1
                end do
                if(ncol < 10)then
                  WRITE (tmp1,'(i1)') ncol
                  slashcol='/'//tmp1
                else if( ncol >= 10 .and. ncol <100)then
                  WRITE (tmp2,'(i2)') ncol
                  slashcol='/'//tmp2          
                else if( ncol >= 100)then
                  WRITE (tmp3,'(i3)') ncol
                  slashcol='/'//tmp3          
                end if
                do jd=1,nacd(jw)
                  icol=icol+1
                  icnt=icnt+1
                  ioname(icnt)=sioname
                  isegn(icnt)=segnum2(1:L2)
                  filename(icnt)=outfn
                  wqparameter(icnt)=CDNAME3(cdn(jd,jw))
                  iounits(icnt)=cdunit(cdn(jd,jw))
                  if(icol < 10)then
                    WRITE (tmp1,'(i1)') icol
                    qcol(icnt)=tmp1//slashcol  
                  else if( icol >= 10)then
                    WRITE (tmp2,'(i2)') icol            
                    qcol(icnt)=tmp2//slashcol
                  end if       
                  outfreq(icnt)=freq
                end do
              
            ENDIF  
          ENDIF  
        ENDDO  
          
        DO JS=1,NGT  ! gates
          IF(IWDO(JWD) == IUGT(JS))THEN  
            JFILE=JFILE+1  
            WRITE(SEGNUM2,'(I0)')IWDO(JWD)  
            SEGNUM2 = ADJUSTL(SEGNUM2)  
            L2      = LEN_TRIM(SEGNUM2)  
            TITLEWITH2='GATE WITHDRAWAL AT SEG'//SEGNUM2(1:L2)  
            WRITE(SEGNUM,'(F10.0)')EGT(JS)  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            TITLEWITH=ADJUSTL(TITLEWITH2)//' ELEV OF WITHDRAWAL CENTERLINE:'//SEGNUM(1:L)  
            WRITE(SEGNUM,'(I0)')JS  
            SEGNUM = ADJUSTL(SEGNUM)  
            L      = LEN_TRIM(SEGNUM)  
            do LL=60,1,-1
              if(TITLEWITH2(LL:LL)/=' ')then
                L4=LL          
                exit
              end if
            end do
            
            ! flow              
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' flow'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='qwo_gate'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='QWD'   
              iounits(icnt)='m^3/s'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq
            
             ! temperature
              icnt=icnt+1
              ioname(icnt)=TITLEWITH2(1:L4)//' temperature'
              isegn(icnt)=segnum2(1:L2)
              filename(icnt)='two_gate'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
              wqparameter(icnt)='T'   
              iounits(icnt)='Celsius'
              qcol(icnt)='1/1'              
              outfreq(icnt)=freq                                                
            
            IF (CONSTITUENTS) THEN                
                 icol=0
                 outfn='cwo_gate'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                 sioname=TITLEWITH2(1:L4)//' constituents'
                 ncol=0           
                 do jc=1,nac
                   ncol=ncol+1
                 end do
                 if(ncol < 10)then
                   WRITE (tmp1,'(i1)') ncol
                   slashcol='/'//tmp1
                 else if( ncol >= 10 .and. ncol <100)then
                   WRITE (tmp2,'(i2)') ncol
                   slashcol='/'//tmp2          
                 else if( ncol >= 100)then
                   WRITE (tmp3,'(i3)') ncol
                   slashcol='/'//tmp3          
                 end if
                 do jc=1,nac
                   icol=icol+1
                   icnt=icnt+1
                   ioname(icnt)=sioname
                   isegn(icnt)=segnum2(1:L2)
                   filename(icnt)=outfn
                   !wqparameter(icnt)=CNAME4(CN(JC))
                   wqparameter(icnt)=CNAME2(CN(JC))   ! cb 12/7/17
                   iounits(icnt)=cunit(cn(jc))        
                   if(icol < 10)then
                     WRITE (tmp1,'(i1)') icol
                     qcol(icnt)=tmp1//slashcol  
                   else if( icol >= 10)then
                     WRITE (tmp2,'(i2)') icol            
                     qcol(icnt)=tmp2//slashcol
                   end if       
                   outfreq(icnt)=freq
                 end do              
            ENDIF  
            IF (DERIVED_CALC) THEN                
                DO JW=1,NWB  
                  IF (IWDO(JWD) >= US(BS(JW)) .AND. IWDO(JWD) <= DS(BE(JW))) EXIT  
                END DO  
                icol=0
                outfn='dwo_gate'//SEGNUM(1:L)//'_seg'//SEGNUM2(1:L2)//'.opt'
                sioname=TITLEWITH2(1:L4)//' derived constituents'                
                ncol=0                  
                do jd=1,nacd(jw)
                  ncol=ncol+1
                end do
                if(ncol < 10)then
                  WRITE (tmp1,'(i1)') ncol
                  slashcol='/'//tmp1
                else if( ncol >= 10 .and. ncol <100)then
                  WRITE (tmp2,'(i2)') ncol
                  slashcol='/'//tmp2          
                else if( ncol >= 100)then
                  WRITE (tmp3,'(i3)') ncol
                  slashcol='/'//tmp3          
                end if
                do jd=1,nacd(jw)
                  icol=icol+1
                  icnt=icnt+1
                  ioname(icnt)=sioname
                  isegn(icnt)=segnum2(1:L2)
                  filename(icnt)=outfn
                  wqparameter(icnt)=CDNAME3(cdn(jd,jw))
                  iounits(icnt)=cdunit(cdn(jd,jw))
                  if(icol < 10)then
                    WRITE (tmp1,'(i1)') icol
                    qcol(icnt)=tmp1//slashcol  
                  else if( icol >= 10)then
                    WRITE (tmp2,'(i2)') icol            
                    qcol(icnt)=tmp2//slashcol
                  end if       
                  outfreq(icnt)=freq
                end do
              
            ENDIF  
          ENDIF  
        ENDDO  
          
      END DO  
    END IF  
 


  do ii=1,icnt
    isegn(ii)=ADJUSTL(isegn(ii))
    L=LEN_TRIM(isegn(ii))
    ioname(ii)=ADJUSTL(ioname(ii))
    L1=LEN_TRIM(ioname(ii))
    wqparameter(ii)=ADJUSTL(wqparameter(ii))
    L2=LEN_TRIM(wqparameter(ii))
    iounits(ii)=ADJUSTL(iounits(ii))
    L3=LEN_TRIM(iounits(ii))
    qcol(ii)=ADJUSTL(qcol(ii))
    L4=LEN_TRIM(qcol(ii))
    filename(ii)=ADJUSTL(filename(ii))
    L5=LEN_TRIM(filename(ii))
    !write(tio,'(a16,a43,a16,a10,a40,f12.6)')ioname(ii),wqparameter(ii),iounits(ii),qcol(ii),filename(ii),outfreq(ii)
    write(tio,152)ioname(ii)(1:L1),isegn(ii)(1:L),wqparameter(ii)(1:L2),iounits(ii)(1:L3),qcol(ii)(1:L4),filename(ii)(1:L5),outfreq(ii)   ! csv format
152   format('"'a,'","',a,'","',a,'","',a,'","',a,'","',a,'",',f12.6)
  end do

  
  close(tio)
  return
    end

!***************************************************************************************
    
    subroutine line_parse(line1,values)
    
    character*2048 line1
    integer ic
    real values(1000)
    character*20 cvalue
    integer icpos(1000)
    
    ic=0
    do i=1,2048
      if(line1(i:i)==',')then  ! finding locations of commas
        ic=ic+1
        icpos(ic)=i
      end if
    end do
        
    do i=1,ic-1  ! assuming value before first comma is blank or character, and ignoring
    
      cvalue=' '
      cvalue=line1((icpos(i)+1):(icpos(i+1)-1))
      cvalue=ADJUSTR(cvalue)
      read(cvalue,'(f20.0)')values(i)      
    end do  
    
    ! in case there is a number after last comma
    cvalue=' '
    cvalue=line1((icpos(ic)+1):(icpos(ic)+21))
    cvalue=ADJUSTR(cvalue)
    read(cvalue,'(f20.0)')values(ic)    
    
    return
    end