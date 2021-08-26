//
//  Common.swift
//  sdk
//
//  Created by Ashish Balhara on 28/06/21.
//

import Foundation
import UIKit

public class SpriteHealthClient {
    internal static var rootController: UIViewController?
    internal static var parentCallback:((NSError?,String) -> Void)?
    internal static var user_identity = "";
    internal static var client_id = "";
    internal static var token_expires_in=1800;
    internal static var currentTokenCreationTime = Date().timeIntervalSinceReferenceDate;
    internal static var primaryColor = ""//"#66d582"
    internal static var primaryTextColor = ""//"#FFFFFF"
    internal static var memberInfo :User?
   // public init(controller: UIViewController) {
   //     SpriteHealthClient.rootController = controller
   //  }
   
    internal static var apiRoot = "https://wpbackendqa.appspot.com"
    internal static var webClientRoot = "https://berger.spritehealth.com"
    static  var auth_token = ""
    static let shared = SpriteHealthClient()
    static var  isInitialized: Bool = false
    private init() {
        
    }
    
    public static func getInstance() -> SpriteHealthClient{
        //isInitialized = true;
        print("Sprite Health Client Instance Granted")
        return shared
    }
    
 
    public func initialize(initOptions: InitOption,  callback : @escaping ((NSError?, String?) -> Void)){
        if ((initOptions.clientId?.isEmpty)!  || initOptions.userIdentity!.isEmpty) {
                   let error = "clientId and userIdentity are mandatory fields of initOptions to initialize."
                let nserror = NSError(domain: error, code: 101, userInfo: nil)
                callback(nserror, nil)
        }
        SpriteHealthClient.client_id = initOptions.clientId!;
        SpriteHealthClient.user_identity = initOptions.userIdentity!
        SpriteHealthClient.currentTokenCreationTime = Date().timeIntervalSinceReferenceDate;
        
        if(initOptions.integrationMode == IntegrationMode.TEST){
            SpriteHealthClient.apiRoot = "https://" + APIDomains.TESTurl
            SpriteHealthClient.webClientRoot = "https://" + WebClientDomains.TESTurl
        }
        else if(initOptions.integrationMode == IntegrationMode.LIVE){
            SpriteHealthClient.apiRoot = "https://" + APIDomains.LIVEurl
            SpriteHealthClient.webClientRoot = "https://" + WebClientDomains.LIVEurl
        }
        
        //createToken()
        //getBrandThemes()
        
        createToken{ (error, result) in
            if let error = error {
                print(error)
                callback(error, nil)
            } else {
                if let list = SpriteHealthClient.convertToDictionary(text:result)as? [String: Any] {
                    if let expires_in = list["expires_in"] as? Int {
                        SpriteHealthClient.token_expires_in = expires_in;
                        }
                    var token_type = "Beearer"
                    if let token_type_loc = list["token_type"] as? String {
                        token_type = token_type_loc;
                        }
                    var access_token = ""
                    if let access_token_loc = list["access_token"] as? String {
                        access_token = access_token_loc;
                        }
                    if(access_token != "") {
                        SpriteHealthClient.auth_token = token_type + " " + access_token
                        
                        self.getMemberDetails(){ (error, result ) in
                                     // do stuff with the result
                                     if let error = error {
                                         print(error)
                                        callback(error, nil)
                                     } else {
                                            if(result != nil)
                                            {
                                                let member = result
                                                SpriteHealthClient.memberInfo = member;
                                                SpriteHealthClient.isInitialized = true;
                                                self.getBrandThemes(){ (error, result ) in
                                                             // do stuff with the result
                                                             if let error = error {
                                                                 print(error)
                                                                callback(error, nil)
                                                             } else {
                                                                
                                                                if let list = SpriteHealthClient.convertToDictionary(text:result)as? [AnyObject] {
                                                                    if let primaryColor = list[0]["primaryColor"] as? String {
                                                                        SpriteHealthClient.primaryColor = primaryColor;
                                                                        SpriteHealthClient.primaryTextColor = "#FFFFFF"
                                                                        }
                                                                   
                                                                }
                                                                
                                                             }
                                                            callback(nil, "Sprite Health instance is initialized successfully")
                                                        }
                                            }
                                        
                                     }
                                }
                     
                    }
                }
            
               
            }
        }
        
    }
    /* public init() {}
     public init(client_id: String, user_identity : String, controller: UIViewController) {
        
        SpriteHealthClient.rootController = controller;
        SpriteHealthClient.client_id = client_id;
        SpriteHealthClient.user_identity = user_identity
        SpriteHealthClient.currentTokenCreationTime = Date().timeIntervalSinceReferenceDate;
        
        createToken()
        getBrandThemes()
    }
    
    public init(auth_token: String, controller: UIViewController) {
        SpriteHealthClient.rootController = controller;
        SpriteHealthClient.auth_token = auth_token;
    } */
    
    // --------------- Private Methods --------------------------
    
    private func getBrandThemes(callback : @escaping ((NSError?, String) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/brandThemes?target=iOS"
        callGetRequest(myurl: url, callback: callback)
    }
    private func createToken(callback : @escaping ((NSError?, String) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/oauth/authorize?response_type=token&client_id=" + SpriteHealthClient.client_id + "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" + SpriteHealthClient.user_identity + "";
        callGetRequest(myurl: url, callback: callback)
    }
    /*
    private func getBrandThemes()
    {
        let myurl = apiRoot + "/resources/brandThemes?target=iOS"
        
        let group = DispatchGroup()
        group.enter()
        //let group = DispatchGroup()
        //group.enter()
        let url = URL(string: myurl)
        guard let requestUrl = url else { fatalError() }
        // Create URL Request
        var request = URLRequest(url: requestUrl)
        let headers = [
            "auth-token" : SpriteHealthClient.auth_token
        ]
        
        request.allHTTPHeaderFields = headers;
        request.httpMethod = "GET"
        request.timeoutInterval = 60
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            
            // Check if Error took place
            if let error = error {
                print("Error took place \(error)")
                return
            }
            
            // Read HTTP Response Status code
            if let response = response as? HTTPURLResponse {
                print("Response HTTP Status code: \(response.statusCode)")
            }
            
            // Convert HTTP Response Data to a simple String
            if let data = data, let dataString = String(data: data, encoding: .utf8) {
                print("Response data string:\n \(dataString)")
                
                if let list = SpriteHealthClient.convertToDictionary(text:dataString)as? [AnyObject] {
                    if let primaryColor = list[0]["primaryColor"] as? String {
                        SpriteHealthClient.primaryColor = primaryColor;
                        SpriteHealthClient.primaryTextColor = "#FFFFFF"
                        }
                   
                }
            }
            group.leave()
            
        }
        task.resume()
        group.wait()
       
    }
 */
    private func createToken()
    {
        let group = DispatchGroup()
        group.enter()
        
        let myurl = SpriteHealthClient.apiRoot + "/resources/oauth/authorize?response_type=token&client_id=" + SpriteHealthClient.client_id + "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" + SpriteHealthClient.user_identity + "";
        let url = URL(string: myurl)
        guard let requestUrl = url else { fatalError() }
        // Create URL Request
        var request = URLRequest(url: requestUrl)
        let headers = [
            "auth-token" : SpriteHealthClient.auth_token
        ]
        
        request.allHTTPHeaderFields = headers;
        request.httpMethod = "GET"
        request.timeoutInterval = 60
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            
            // Check if Error took place
            if let error = error {
                print("Error took place \(error)")
                group.leave()
                return
            }
            
            // Read HTTP Response Status code
            if let response = response as? HTTPURLResponse {
                print("Response HTTP Status code: \(response.statusCode)")
            }
            
            // Convert HTTP Response Data to a simple String
            if let data = data, let dataString = String(data: data, encoding: .utf8) {
                //print("Response data string:\n \(dataString)")
                
                if let list = SpriteHealthClient.convertToDictionary(text:dataString)as? [String: Any] {
                    if let expires_in = list["expires_in"] as? Int {
                        SpriteHealthClient.token_expires_in = expires_in;
                        }
                    var token_type = "Beearer"
                    if let token_type_loc = list["token_type"] as? String {
                        token_type = token_type_loc;
                        }
                    var access_token = ""
                    if let access_token_loc = list["access_token"] as? String {
                        access_token = access_token_loc;
                        }
                    if(access_token != "") {
                        SpriteHealthClient.auth_token = token_type + " " + access_token
                        group.leave()
                      
                    }
                }
            }
            
            
        }
        task.resume()
        group.wait()
    }
   
    
    
    private func callGetRequest(myurl:String, callback : @escaping ((NSError?, String) -> Void))
    {
        if((SpriteHealthClient.client_id != "") && (SpriteHealthClient.user_identity != "") && SpriteHealthClient.token_expires_in != 0)
        {
            
            let currentTokenCreationTime = SpriteHealthClient.currentTokenCreationTime;
            let currentTime = Date().timeIntervalSinceReferenceDate;
            if(Int(( currentTime - currentTokenCreationTime)) > SpriteHealthClient.token_expires_in )
            {
                createToken()
            }
            let url = URL(string: myurl)
            guard let requestUrl = url else { fatalError() }
            // Create URL Request
            var request = URLRequest(url: requestUrl)
            let headers = [
                "auth-token" : SpriteHealthClient.auth_token
            ]
            
            request.allHTTPHeaderFields = headers;
            request.httpMethod = "GET"
            request.timeoutInterval = 60
            let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
                
                // Check if Error took place
                if let error = error {
                    print("Error took place \(error)")
                    callback(error as NSError,"")
                    return
                }
                
                // Read HTTP Response Status code
                if let response = response as? HTTPURLResponse {
                    print("Response HTTP Status code: \(response.statusCode)")
                }
                
                // Convert HTTP Response Data to a simple String
                if let data = data, let dataString = String(data: data, encoding: .utf8) {
                    //print("Response data string:\n \(dataString)")
                    callback(nil,dataString)
                }
                
            }
            task.resume()
        }
    }
   
    
    private func stringFromAny(_ value:Any?) -> String {

        if let nonNil = value, !(nonNil is NSNull) {

            return String(describing: nonNil) // "Optional(12)"
        }
        return ""
    }
    
    private func callPostRequest(myurl:String, requestParameters: [String: Any] ,callback : @escaping ((NSError?,String) -> Void))
    {
        if((SpriteHealthClient.client_id != "") && (SpriteHealthClient.user_identity != "") && SpriteHealthClient.token_expires_in != 0)
        {
            
            let currentTokenCreationTime = SpriteHealthClient.currentTokenCreationTime;
            let currentTime = Date().timeIntervalSinceReferenceDate;
            if(Int(( currentTime - currentTokenCreationTime)) > SpriteHealthClient.token_expires_in )
            {
                createToken()
            }
      
        
            let url = URL(string: myurl)
            guard let requestUrl = url else { fatalError() }
            // Create URL Request
            var request = URLRequest(url: requestUrl)
            let headers = [
                "auth-token" : SpriteHealthClient.auth_token,
                "Content-Type": "application/x-www-form-urlencoded"
            ]
            
            request.allHTTPHeaderFields = headers;
            request.httpMethod = "POST"
            request.timeoutInterval = 60
           
            
            var requestBodyComponent = URLComponents()
           
            requestBodyComponent.queryItems = requestParameters.map {
                URLQueryItem(name: $0.key, value:  stringFromAny($0.value))
            }
         
            let httpBody = requestBodyComponent.query?.data(using: .utf8)
            
            request.httpBody = httpBody
            let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
                
                // Check if Error took place
                if let error = error {
                    print("Error took place \(error)")
                    return
                }
                
                // Read HTTP Response Status code
                if let response = response as? HTTPURLResponse {
                    print("Response HTTP Status code: \(response.statusCode)")
                }
                
                // Convert HTTP Response Data to a simple String
                if let data = data, let dataString = String(data: data, encoding: .utf8) {
                    //print("Response data string:\n \(dataString)")
                    callback(nil,dataString)
                }
                
            }
            task.resume()
        }
    }
    
    
    
    
    
    
    // ------------------------- Static Function --------------------
    static internal func gobackToParent()
    {
        SpriteHealthClient.parentCallback!(nil, "test string");
    }
    internal static func convertToDictionary(text: String)  -> AnyObject? {

     if let data = text.data(using: .utf8) {
         //do {
            // return try JSONSerialization.jsonObject(with: data, options: []) as? Any
         
             let json = try? JSONSerialization.jsonObject(with: data as Data, options: []) as? AnyObject
            return json
        /* } catch let error {
             print(error.localizedDescription)
         }*/
     }

     return nil
    }
    internal static func hexStringToUIColor (hex:String) -> UIColor {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }

        if ((cString.count) != 6) {
            return UIColor.gray
        }

        var rgbValue:UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)

        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
    
    
    
    // ------------------------ API Methods --------------------------
    
    private func setToken(authToken: String)
    {
        SpriteHealthClient.auth_token = authToken
    }
    internal func getToken() -> String
    {
        return SpriteHealthClient.auth_token
    }
   
    /// Gets profile details of current logged-in user/member
    ///- parameter callback : Callback object with onSuccess and onError events. onSuccess returns [User] object
    public func getMemberDetails (callback : @escaping ((NSError?,User?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/user?withCoverage=true";
        //callGetRequest(myurl: url, callback: callback)
        
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as User?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                           do {
                               let member = try User(dictionary: json)
                                callback(nil,member)
                           } catch {
                               print(error)
                           }
                       
                    }
            }
        }
    }
    
    /// Gets list of members ([User]) of a family by familyId.
    /// - parameter familyId : familyId
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns list of members [User]
    public func getFamilyMembers(familyId: String ,callback : @escaping ((NSError?,[User]?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "resources/user/family/members?familyId=" + familyId;
        //callGetRequest(myurl: url, callback: callback)
        
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as [User]?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [[String: Any]] {
                           //do {
                            let members =  try? JSONDecoder().decode([User].self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                           /*} catch {
                            callback(error as NSError,nil as [User]?)
                           } */
                       
                    }
            }
        }
    }
    /// Returns list of specialists with their first availability.
    /// - parameter state : HashMap of parameters
    ///
    ///e.g. of queryParam is :
    ///
    ///  [
    ///
    ///    "specialities": 26               --required
    ///
    ///    "serviceDefinitionIds": 5414975176704000               --required
    ///
    ///    "startDateTime": 06/22/2021 10:39 am                --required
    ///
    ///    "currentTime": 12:39:59               --required
    ///
    ///    "startIndex": 0              --recommended
    ///
    ///    "endIndex": 10              --recommended
    ///
    ///    "getOnlyFirstAvailability": true              --recommended
    ///    
    ///    "networkIds": 5783379589988352,6214613415755776
    ///
    ///     "state" : "TX"
    ///    ]
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns list of specialists [Specialist] with available slots for service
    public func getAvailableSpecialists(queryParams: [String:Any],callback : @escaping ((NSError?,[Specialist]?) -> Void))
    {
      
        
        
        
        var url = SpriteHealthClient.apiRoot + "/resources/specialists/available"//?specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=10&getOnlyFirstAvailability=true&networkIds="+networkIds + "&state=" + location
       
        //callGetRequest(myurl: url, callback: callback)
        url = createURLFromParams(url: url, queryParams: queryParams)
        
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as [Specialist]?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [[String: Any]] {
                          // do {
                            let specialist = try! JSONDecoder().decode([Specialist].self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,specialist)
                          /* } catch {
                            callback(error as NSError,nil as [Specialist]?)
                           }
                            */
                    }
            }
        }
        
    }
    
    /// Gets specialist details ([Specialist]) by id.
    /// - parameter specialistId : id of specialist/vendorUser
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns [Specialist] object
    public func getSpecialistDetails(specialistId: Int, callback : @escaping ((NSError?,Specialist?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/user/specialists/" + String(specialistId) + "?slim=HIGH";
        
        
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as Specialist?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                            let members = try! JSONDecoder().decode(Specialist.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                           /*} catch {
                            callback(error as NSError,nil as Specialist?)
                           }
                           */
                    }
            }
        }
    }
    
    /// Gets service details ([Service]) by id.
    /// - parameter serviceId : id of service
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns [Service] object
    public func getServiceDetails(serviceId: Int, callback : @escaping ((NSError?,Service?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/services/" + String(serviceId) + "?slim=HIGH";
        
        
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as Service?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                             let members = try! JSONDecoder().decode(Service.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as Service?)
                           } */
                       
                    }
            }
        }
    }
    
    /// Gets list of all supported specialities ([Speciality]).
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns list of all [Speciality]
    public func getSpecialities( callback : @escaping ((NSError?,[Speciality]?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/file/JSON/specialities.json"
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as [Speciality]?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [[String: Any]] {
                          // do {
                            let members = try! JSONDecoder().decode([Speciality].self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as [Speciality]?)
                           } */
                       
                    }
            }
        }
    }
    
    /// Gets availability i.e. free slots of specialist by specialistId and other filters
    /// - parameter queryParams : HashMap of parameters
    /// queryParams e.g. :
    /// [
    ///"serviceId": 6207822929854464              --required
    ///
    ///"vendorUserId": 6267591627636736              --required
    ///
    ///"weeks": 3              --required
    ///
    ///"startDateTime": 06/22/2021 11:10 am              --required
    ///
    ///"currentTime": 13:10:30              --required
    ///  ]
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns the list of [SpecialistAvailability].
    public func getSpecialistAvailability(queryParams: [String: Any],callback : @escaping ((NSError?,SpecialistAvailability?) -> Void))
    {
        var url = SpriteHealthClient.apiRoot + "/resources/specialists/available"//?serviceId="+serviceId+"&vendorUserId=" + specialistId + "&weeks=2"
        url = createURLFromParams(url: url, queryParams: queryParams)
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as SpecialistAvailability?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                            let members = try! JSONDecoder().decode(SpecialistAvailability.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as SpecialistAvailability?)
                           } */
                       
                    }
            }
        }
    }
    
    
    /// Computes network coverage ([NetworkCoverage] ) of a service for a member given a provider and service code
    /// - parameter formPost : HashMap of parameters
    /// e.g. of formPost :
    /// [
    /// "memberId": 6282717383622656               --required
    ///
    /// "providerId": 5438400756711424               --required
    ///
    /// "serviceCode": "CPT 97110 "              --required
    ///
    /// "termType": "Purchase"               --required
    ///
    /// "specialistId": 6267591627636736               --required
    ///
    /// "operation": "COMPUTE"               --required
    ///
    /// "units": 1
    ///
    /// "organizationId": 4710567524696064
    ///
    /// "specialityNames": "Physical Therapist"
    ///
    /// ]
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns the list of [SpecialistAvailability].
    
    public func getServiceNetworkCoverage(formPost: [String:Any] ,callback : @escaping ((NSError?,NetworkCoverage?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/serviceCoverages/fetchMAA"
        //callPostRequest(myurl: url, requestParameters: requestParameters,callback: callback)
        callPostRequest(myurl: url,requestParameters: formPost){ (error, result) in
            if let error = error {
                callback(error,nil as NetworkCoverage?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                            let members = try! JSONDecoder().decode(NetworkCoverage.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as NetworkCoverage?)
                           } */
                       
                    }
            }
        }
        
    }
    
    private func createURLFromParams(url: String, queryParams: [String:Any]) -> String
    {
        var urlParams = ""
        var newurl = url;
        for queryParam in queryParams {
            if(urlParams == "")
            {
            urlParams = queryParam.key + "=" + stringFromAny( queryParam.value).addingPercentEncoding(withAllowedCharacters: NSMutableCharacterSet.urlQueryAllowed)!
            }
            else {
                urlParams = urlParams + "&" + queryParam.key + "=" + stringFromAny( queryParam.value).addingPercentEncoding(withAllowedCharacters: NSMutableCharacterSet.urlQueryAllowed)!
            }
        }
        if(urlParams != "") {
            newurl = newurl + "?" + urlParams
        }
        return newurl
    }
    
    /// Gets list of reasons/issues ([Reason]).
    /// - parameter queryParams : HashMap of parameters e.g.
    /// e.g. queryParams :
    ///
    /// [
    ///
    ///     "specialities" = "26,37"               --Optional
    ///
    /// ]
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns list of services.
    
    public func getReasons(queryParams:[String: Any], callback : @escaping ((NSError?,[Reason]?) -> Void))
    {
        
        var url = SpriteHealthClient.apiRoot + "/resources/reasons"//specialities=" + specialities + "&slim=HIGH"
       
        url = createURLFromParams(url: url, queryParams: queryParams)
        
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as [Reason]?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [[String: Any]] {
                          // do {
                            let members = try! JSONDecoder().decode([Reason].self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as [Reason]?)
                           } */
                       
                    }
            }
        }
    }
    
    /// Gets list of services ([Service]) by reasonId.
    /// - parameter reasonId : id of reason object
    /// - parameter providerId :id of vendor/provider object
    /// - parameter callback : Callback object with onSuccess and onError events. onSuccess event returns list of services.
    public func getServicesByReason(reasonId: Int, providerId: Int,callback : @escaping ((NSError?,[Service]?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/reasons/" + String(reasonId) + "/services?vendorId=" + String(providerId);
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as [Service]?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [[String: Any]] {
                          // do {
                            let members = try! JSONDecoder().decode([Service].self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as [Service]?)
                           } */
                       
                    }
            }
        }
    }
    
    
    
   func getDeveloperAccount( callback : @escaping ((NSError?,DeveloperAccount?) -> Void))
    {
    var url = SpriteHealthClient.apiRoot + "/resources/developerAccounts"
        if(SpriteHealthClient.client_id != ""){
            url = url + "?clientId=" + SpriteHealthClient.client_id
        }
       
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as DeveloperAccount?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                            let members = try! JSONDecoder().decode(DeveloperAccount.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as DeveloperAccount?)
                           } */
                       
                    }
            }
        }
    }
    
    
    internal func getServicePricing(serviceId: String, vendorId: String,callback : @escaping ((NSError?,String) -> Void)){
        let url = SpriteHealthClient.apiRoot + "/resources/search/services?vendorId=" + vendorId + "&vendorUserId=&id=" + serviceId;
        callGetRequest(myurl: url, callback: callback)
        
    }
    
    /// Creates appointment ([Appointment]).
    /// - parameter formPost : HashMap of parameters
    /// e.g. of formPost :
    /// [
    /// "vendorUserId": 6267591627636736                --required
    ///
    /// "providerName": "Anne Wachsmann"                --required
    ///
    /// "serviceId": 6207822929854464                --required
    ///
    /// "serviceName": "Virtual Physical Therapy "               --required
    ///
    /// "patientId": 6282717383622656               --required
    ///
    /// "serviceSetting": "Telehealth"               --required
    ///
    /// "eventStartTime": "06/22/2021 02:15 pm"                --required
    ///
    /// "eventEndTime": "06/22/2021 03:00 pm "              --required
    ///
    /// "status": "booked"               --required
    ///
    /// "customerPhone": "+1 27808xxxx08 "             --required
    ///
    /// "reasonId": 6272244076511232
    ///
    /// "reasonName": "Ankle pain"
    ///
    /// "where": "address or other details"
    ///
    /// "walletId": 6317246169219072
    ///
    /// ]
    /// - parameter callback : Callback object with onSuccess and onError events. OnSuccess event returns appointment object.
    public func createAppointment(formPost: [String: Any], callback : @escaping ((NSError?,CalendarEvent?) -> Void))
    {
        let url =  SpriteHealthClient.apiRoot + "/resources/calendar/event"
        if (!JSONSerialization.isValidJSONObject(formPost)) {
               print("is not a valid json object")
               return
           }
        //callPostRequest(myurl: myurl, requestParameters: formPost,callback: callback)
        callPostRequest(myurl: url,requestParameters: formPost){ (error, result) in
            if let error = error {
                callback(error,nil as CalendarEvent?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                            let members = try! JSONDecoder().decode(CalendarEvent.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as CalendarEvent?)
                           } */
                       
                    }
            }
        }
    }

    /// Get appointment details ([Appointment]) by appointmentId.
    /// - parameter appointmentId : id of appointment
    /// - parameter callback : Callback object with onSuccess and onError events.  OnSuccess event returns appointment object.
    public func getAppointmentDetails( appointmentId: Int,callback : @escaping ((NSError?,Appointment?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/appointments/" + String(appointmentId)
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as Appointment?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                         //  do {
                            let members = try! JSONDecoder().decode(Appointment.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as Appointment?)
                           } */
                       
                    }
            }
        }
    }
    
    /// Gets list of appointments ([Appointment]) for current logged-in user by fetchMode.
    /// - parameter fetchMode : upcoming, all
    /// - parameter callback : Callback object with onSuccess and onError events. OnSuccess event returns list of appointments
   
    public func getMyAppointments( mode: String,callback : @escaping ((NSError?,[Appointment]?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/appointment/myappointments?fetchMode=" + mode
        //callGetRequest(myurl: url, callback: callback)
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as [Appointment]?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [[String: Any]] {
                          // do {
                            let members = try! JSONDecoder().decode([Appointment].self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as [Appointment]?)
                           } */
                       
                    }
            }
        }
    }
    
    /// Gets list of appointments ([Appointment]) by patientId and fetchMode.
    /// - parameter patientId : id of member
    /// - parameter fetchMode : upcoming, all
    /// - parameter callback : Callback object with onSuccess and onError events. OnSuccess event returns list of appointments
    public func getAppointments( patientId: Int,callback : @escaping ((NSError?,Appointment?) -> Void))
    {
        let url = SpriteHealthClient.apiRoot + "/resources/appointment?patientId=" + String(patientId)
        callGetRequest(myurl: url){ (error, result) in
            if let error = error {
                callback(error,nil as Appointment?)
            } else {
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let results = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                          // do {
                            let members = try! JSONDecoder().decode(Appointment.self, from: JSONSerialization.data(withJSONObject: results))
                               //let member = try User(dictionary: json)
                                callback(nil,members)
                          /* } catch {
                            callback(error as NSError,nil as Appointment?)
                           } */
                       
                    }
            }
        }
    }
   
    
    /// Launches the UI flow for Virtual Physical Therapy Care
    /// - parameter controller: To start new controller over it
    /// - parameter callback : Callback object with onSuccess and onError events. OnSuccess event returns list of information of booked appointment
    
    public func  launchVPTFlow(controller: UIViewController,callback : @escaping ((NSError?,String) -> Void)) {
        
        SpriteHealthClient.rootController = controller;
        SpriteHealthClient.parentCallback = callback;
        let newViewController = VPTFinderViewController()
        newViewController.modalPresentationStyle = .fullScreen
        SpriteHealthClient.rootController?.present(newViewController, animated:true,completion:nil)
    }
    
}
