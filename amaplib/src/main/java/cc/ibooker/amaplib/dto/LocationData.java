package cc.ibooker.amaplib.dto;

/**
 * 定位信息
 *
 * @author 邹峰立
 */
public class LocationData {
    private double pointy;//获取纬度
    private double pointx;//获取经度
    private String currentAddress;//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
    private String currentCountry;//国家信息
    private String currentProv;//省信息
    private String currentCity;//城市信息
    private String currentDistrict;//城区信息
    private String currentStreet;//街道信息
    private String currentStreetNum;//街道门牌号信息
    private String currentCityCode;//城市编码
    private String currentAdCode;//地区编码
    private String currentAoiName;//获取当前定位点的AOI信息

    public LocationData() {
        super();
    }

    public LocationData(double pointy, double pointx, String currentAddress, String currentCountry, String currentProv, String currentCity, String currentDistrict, String currentStreet, String currentStreetNum, String currentCityCode, String currentAdCode, String currentAoiName) {
        this.pointy = pointy;
        this.pointx = pointx;
        this.currentAddress = currentAddress;
        this.currentCountry = currentCountry;
        this.currentProv = currentProv;
        this.currentCity = currentCity;
        this.currentDistrict = currentDistrict;
        this.currentStreet = currentStreet;
        this.currentStreetNum = currentStreetNum;
        this.currentCityCode = currentCityCode;
        this.currentAdCode = currentAdCode;
        this.currentAoiName = currentAoiName;
    }

    public double getPointy() {
        return pointy;
    }

    public void setPointy(double pointy) {
        this.pointy = pointy;
    }

    public double getPointx() {
        return pointx;
    }

    public void setPointx(double pointx) {
        this.pointx = pointx;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentCountry() {
        return currentCountry;
    }

    public void setCurrentCountry(String currentCountry) {
        this.currentCountry = currentCountry;
    }

    public String getCurrentProv() {
        return currentProv;
    }

    public void setCurrentProv(String currentProv) {
        this.currentProv = currentProv;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getCurrentDistrict() {
        return currentDistrict;
    }

    public void setCurrentDistrict(String currentDistrict) {
        this.currentDistrict = currentDistrict;
    }

    public String getCurrentStreet() {
        return currentStreet;
    }

    public void setCurrentStreet(String currentStreet) {
        this.currentStreet = currentStreet;
    }

    public String getCurrentStreetNum() {
        return currentStreetNum;
    }

    public void setCurrentStreetNum(String currentStreetNum) {
        this.currentStreetNum = currentStreetNum;
    }

    public String getCurrentCityCode() {
        return currentCityCode;
    }

    public void setCurrentCityCode(String currentCityCode) {
        this.currentCityCode = currentCityCode;
    }

    public String getCurrentAdCode() {
        return currentAdCode;
    }

    public void setCurrentAdCode(String currentAdCode) {
        this.currentAdCode = currentAdCode;
    }

    public String getCurrentAoiName() {
        return currentAoiName;
    }

    public void setCurrentAoiName(String currentAoiName) {
        this.currentAoiName = currentAoiName;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "pointy=" + pointy +
                ", pointx=" + pointx +
                ", currentAddress='" + currentAddress + '\'' +
                ", currentCountry='" + currentCountry + '\'' +
                ", currentProv='" + currentProv + '\'' +
                ", currentCity='" + currentCity + '\'' +
                ", currentDistrict='" + currentDistrict + '\'' +
                ", currentStreet='" + currentStreet + '\'' +
                ", currentStreetNum='" + currentStreetNum + '\'' +
                ", currentCityCode='" + currentCityCode + '\'' +
                ", currentAdCode='" + currentAdCode + '\'' +
                ", currentAoiName='" + currentAoiName + '\'' +
                '}';
    }
}
