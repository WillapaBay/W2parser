package hec2.wat.plugin.ceQualW2.w2parser;

/**
 * Global Card Names
 */
public class CardNames {
    static final String Calculations                                         = "CALCULAT";
    static final String ConstituentComputations                              = "CST COMP";
    static final String TimestepControl                                      = "DLT CON";
    static final String DistributedTributaries                               = "DST TRIB";
    static final String WithdrawalSegment                                    = "WD SEG";
    static final String WithdrawalOutputDate                                 = "WITH DAT"; // WDO DATE
    static final String TimeSeriesSegment                                    = "TSR SEG";
    static final String TimeSeriesLayer                                      = "TSR LAYE"; // The manual shows "TSR ELEV, but the W2 GUI names the card "TSR LAYE"
    static final String InflowActiveConstituentControl                       = "CIN CON";
    static final String RestartDate                                          = "RSO DATE";
    static final String RestartFrequency                                     = "RSO FREQ";
    static final String Location                                             = "LOCATION";
    static final String NumberStructures                                     = "N STRUC";


    // Filenames
    static final String BathymetryFilenames                                  = "BTH FILE";
    static final String MeteorologyFilenames                                 = "MET FILE";
    static final String BranchInflowFilenames                                = "QIN FILE";
    static final String BranchInflowTemperatureFilenames                     = "TIN FILE";
    static final String BranchInflowConcentrationFilenames                   = "CIN FILE";
    static final String StructuralWithdrawalFilenames                        = "QOT FILE";
    static final String WithdrawalFilenames                                  = "QWD FILE"; // Lateral withdrawals
    static final String TributaryInflowFilenames                             = "QTR FILE";
    static final String TributaryInflowTemperatureFilenames                  = "TTR FILE";
    static final String TributaryInflowConcentrationFilenames                = "CTR FILE";
    static final String DistributedTributaryInflowFilenames                  = "QDT FILE";
    static final String DistributedTributaryInflowTemperatureFilenames       = "TDT FILE";
    static final String DistributedTributaryInflowConcentrationFilenames     = "CDT FILE";
    static final String PrecipitationFilenames                               = "PRE FILE";
    static final String PrecipitationTemperatureFilenames                    = "TPR FILE";
    static final String PrecipitationConcentrationFilenames                  = "CPR FILE";
}
