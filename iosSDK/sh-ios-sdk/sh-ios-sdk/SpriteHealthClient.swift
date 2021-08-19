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
    internal static var memberInfo :[String: Any]?
    public init(controller: UIViewController) {
        SpriteHealthClient.rootController = controller
     }
   
    private let apiRoot = "https://wpbackendqa.appspot.com"
    static  var auth_token = ""
    public init() {}
    public init(client_id: String, user_identity : String, controller: UIViewController) {
        
        SpriteHealthClient.rootController = controller;
        SpriteHealthClient.client_id = client_id;
        SpriteHealthClient.user_identity = user_identity
        SpriteHealthClient.currentTokenCreationTime = Date().timeIntervalSinceReferenceDate;
        
        createToken()
        getThemeColors()
    }
    
    public init(auth_token: String, controller: UIViewController) {
        SpriteHealthClient.rootController = controller;
        SpriteHealthClient.auth_token = auth_token;
    }
    
    // --------------- Private Methods --------------------------
    private func getThemeColors()
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
    private func createToken()
    {
        let group = DispatchGroup()
        group.enter()
        
        let myurl = apiRoot + "/resources/oauth/authorize?response_type=token&client_id=" + SpriteHealthClient.client_id + "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" + SpriteHealthClient.user_identity + "";
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
        let task = URLSession.shared.dataTask(with: request) { [self] (data, response, error) in
            
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
                print("Response data string:\n \(dataString)")
                
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
                        
                                memberDetail(){ (error, result) in
                                     // do stuff with the result
                                     if let error = error {
                                         print(error)
                                        group.leave()
                                     } else {
                                     let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                                         if let json = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [String: Any] {
                                             SpriteHealthClient.memberInfo = json;
                                            print(json)
                                         }
                                        group.leave()
                                     }
                                }
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
                    return
                }
                
                // Read HTTP Response Status code
                if let response = response as? HTTPURLResponse {
                    print("Response HTTP Status code: \(response.statusCode)")
                }
                
                // Convert HTTP Response Data to a simple String
                if let data = data, let dataString = String(data: data, encoding: .utf8) {
                    print("Response data string:\n \(dataString)")
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
                    print("Response data string:\n \(dataString)")
                    callback(nil,dataString)
                }
                
            }
            task.resume()
        }
    }
    
    
    
    
    // ------------------------- Static Function --------------------
    static public func gobackToParent()
    {
        SpriteHealthClient.parentCallback!(nil, "test string");
    }
    static func convertToDictionary(text: String) -> AnyObject? {

     if let data = text.data(using: .utf8) {
         do {
            // return try JSONSerialization.jsonObject(with: data, options: []) as? Any
         
            let json = try? JSONSerialization.jsonObject(with: data as Data, options: []) as? AnyObject
            return json
         } catch {
             print(error.localizedDescription)
         }
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
    
    public func setToken(authToken: String)
    {
        SpriteHealthClient.auth_token = authToken
    }
    public func getToken() -> String
    {
        return SpriteHealthClient.auth_token
    }
    public func memberDetail (callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/user?withCoverage=true";
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func familyMembers(familyId: String ,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "resources/user/family/members?familyId=" + familyId;
        callGetRequest(myurl: url, callback: callback)
    }
    public func specialistAvailable(callback : @escaping ((NSError?,String) -> Void))
    {
      
        var currentDate = Date()
        let calendar = Calendar.current
        let hour = calendar.component(.hour, from: currentDate)
        
        if(hour >= 22)
        {
            var dateComponent = DateComponents()
            dateComponent.hour = 3
            currentDate = Calendar.current.date(byAdding: dateComponent, to: currentDate)!
        }
        else {
            var dateComponent = DateComponents()
            dateComponent.hour = 2
            currentDate = Calendar.current.date(byAdding: dateComponent, to: currentDate)!
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd, yyyy hh:mm aaa"
        let startDateTime = dateFormatter.string(from: currentDate)
        dateFormatter.dateFormat = "HH:mm:ss"
        let currentTime = dateFormatter.string(from: currentDate)
        var networkIds = ""
        if let planSubscriptions = SpriteHealthClient.memberInfo!["planSubscriptions"] as? [NSDictionary] {
            for planSubscription in planSubscriptions
            {
                if let planNetworks = planSubscription["networkIds"] as? [Int]
                {
                    for planNetwork in planNetworks {
                        if(networkIds == "") {
                            networkIds = String(planNetwork)
                        }
                        else {
                            networkIds = networkIds + "," + String(planNetwork)
                        }
                    }
                }
                
            }
            
        }
        var location = "";
        if let officeLocations = SpriteHealthClient.memberInfo!["officeLocations"] as? [NSDictionary] {
            for officeLocation in officeLocations
            {
                if let state = officeLocation["stateOrProvince"] as? String
                {
                    location = state
                    break;
                }
                
            }
            
        }
        
        var url = apiRoot + "/resources/specialists/available?specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=10&getOnlyFirstAvailability=true&networkIds="+networkIds + "&state=" + location
        
        let timeFields = "&startDateTime=" + startDateTime + "&currentTime"+currentTime //
        url = url + timeFields.addingPercentEncoding(withAllowedCharacters: NSMutableCharacterSet.urlQueryAllowed)!
        callGetRequest(myurl: url, callback: callback)
    }
    
    
    public func specialistDetail(specialistId: String, callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/user/specialists/" + specialistId + "?slim=HIGH";
        
        
        callGetRequest(myurl: url, callback: callback)
    }
   
    public func fetchServiceCoverage(requestParameters: [String:Any] ,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/serviceCoverages/fetchMAA"
        callPostRequest(myurl: url, requestParameters: requestParameters,callback: callback)
        
    }
    public func specialistAvailableSlot(specialistId: String, serviceId: String,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/specialists/available?serviceId="+serviceId+"&vendorUserId=" + specialistId + "&weeks=2"
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func fetchReasonsBySpecialities(specialities:String, callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/reasons?specialities=" + specialities + "&slim=HIGH"
        callGetRequest(myurl: url, callback: callback)
    }
    public func listOfServiceByReason(reasonId: String, specialistId: String,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/reasons/" + reasonId + "/services?vendorId=" + specialistId;
        callGetRequest(myurl: url, callback: callback)
    }
    
    
    public func getServicePricing(serviceId: String, vendorId: String,callback : @escaping ((NSError?,String) -> Void)){
        let url = apiRoot + "/resources/search/services?vendorId=" + vendorId + "&vendorUserId=&id=" + serviceId;
        callGetRequest(myurl: url, callback: callback)
        
    }
    public func appointmentBooking(requestParameters: [String: Any], callback : @escaping ((NSError?,String) -> Void))
    {
        let myurl =  apiRoot + "/resources/calendar/event"
        if (!JSONSerialization.isValidJSONObject(requestParameters)) {
               print("is not a valid json object")
               return
           }
        callPostRequest(myurl: myurl, requestParameters: requestParameters,callback: callback)
        
    }

    public func appointmentDetails( appointmentId: Int,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/appointments/" + String(appointmentId)
        callGetRequest(myurl: url, callback: callback)
    }
    // mode =  upcoming, all
    public func listOfAppointments( mode: String,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/appointment/myappointments?fetchMode=" + mode
        callGetRequest(myurl: url, callback: callback)
    }
    
    // with patients
    public func appointmentsOfPatients( patientId: Int,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/appointment?patientId=" + String(patientId)
        callGetRequest(myurl: url, callback: callback)
    }
    public func fetchSpecialities( callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/file/JSON/specialities.json"
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func getDeveloperAccount( callback : @escaping ((NSError?,String) -> Void))
    {
        var url = apiRoot + "/resources/developerAccounts"
        if(SpriteHealthClient.client_id != ""){
            url = url + "?clientId=" + SpriteHealthClient.client_id
        }
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func  callVPTFinder(callback : @escaping ((NSError?,String) -> Void)) {
        SpriteHealthClient.parentCallback = callback;
        let newViewController = VPTFinderViewController()
        newViewController.modalPresentationStyle = .fullScreen
        SpriteHealthClient.rootController?.present(newViewController, animated:true,completion:nil)
    }
    
}
