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
    public init(controller: UIViewController) {
        SpriteHealthClient.rootController = controller
     }
    // /resources/oauth/authorize?response_type=token&client_id=0b5c8d72f9794ec69870886cd060bc82&no_redirect=true&skip_user_auth=true&user_identity=dag@berger.com
    //private let apiRoot = "https://oauth-dot-wpbackendqa.appspot.com"
    private let apiRoot = "https://wpbackendqa.appspot.com"
    //static  var auth_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.bUJjUm1xREJ0LzAzQ2hKSWh2by9jRnA4V0dsRkRnQ1c2cDFlNndvQ2xUbmZPajZSRW9aYkNSZmIrWlNVT1ZnMVBRcDVCRVlERDlpWHFiTlR4M2M3TjkvS1ZEUm8yeFQ1WTJrVGxmNjFHditDUUNXSFdkMDZBRElPYnpsZGJHTG9tb1lxYlVBaUMyMTlaRjJCNSsvQ1c5TUhhaFhJcllRM1J4ZEZtT0s0cHF0WElDVUJHc0tOL1lLR0pML1pKdFZBUlRlREVaaUprQVpWaHlUT21kQnZiejhvYzNGKytVQnlucHJodGh3SlY4dFAyWkh6a3lJYk5OOTVmMysrWnJTK1ZKa1NsMVpsS2tndWU1NkNCMmFySkFsalQyWkErS1gvbUpEMmdHUjhMNkdvY1UyOXBCd3RJeTNHMS9uOUlNN0hWSURJWmpJdk5CNnYzNjJVM0gwREtVNzdmSmhSTUZ4ZmRoc3B4QkR1b0VWNCt0bGVUNE9QN251Z2NwWjhSV0hSK2pDOGZWc2ZXZ2x6bENQQTVnUTBmZzJLNWpoaVdLMkVPYUpweHIyMDllKy9vaUFJV3U0WUpYeHhrbk5GbTJ4Rk5xbnEzSm5NS0VCbmo3UXRIYlpGaXNnbVdidS9OQnFTTUFwalBXUVFnMkVXT2RGeGMwdGhaTFpaOEZIRUpGUXVJU3AyOHJ4VkVSZkVXbEEvZ2U1RWlwVGcwZzBzZlVSVg.ARVDXusPZ4jkhC35sVior7zGzNlvePVis/bvpDZLo1M="
    static  var auth_token = ""
    public init() {}
    
    //let myurl = "https://wpbackendqa.appspot.com/resources/specialists/available?specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=10&startDateTime=06/21/2021 12:18 pm&currentTime=14:18:39&getOnlyFirstAvailability=true&networkIds=5783379589988352,6214613415755776,6206424134713344,6293602384740352,6306161213046784,6206424134713344,5783379589988352,6293602384740352"
    
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
                    }
                }
            }
            group.leave()
            
        }
        task.resume()
        group.wait()
    }
    public func setToken(authToken: String)
    {
        SpriteHealthClient.auth_token = authToken
    }
    public func getToken() -> String
    {
        return SpriteHealthClient.auth_token
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
           /* let parameters: [String: Any] = [
                    "request": [
                            "xusercode" : "YOUR USERCODE HERE",
                            "xpassword": "YOUR PASSWORD HERE"
                    ]
                ]
            */
            
            var requestBodyComponent = URLComponents()
           
            requestBodyComponent.queryItems = requestParameters.map {
                URLQueryItem(name: $0.key, value:  stringFromAny($0.value))
            }
          /*  guard let httpBody = try? JSONSerialization.data(withJSONObject: parameters, options: []) else {
                    return
                }
     */
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
        let url = apiRoot + "/resources/specialists/available?specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=10&getOnlyFirstAvailability=true&networkIds=5783379589988352,6214613415755776,6206424134713344,6293602384740352,6306161213046784,6206424134713344,5783379589988352,6293602384740352"
        
        callGetRequest(myurl: url, callback: callback)
    }
    public func specialistDetail(specialistId: String, callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/user/specialists/" + specialistId + "?slim=HIGH";
        
        
        callGetRequest(myurl: url, callback: callback)
    }
    public func reasonList(callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/reasons?specialities=26"
        callGetRequest(myurl: url, callback: callback)
    }
    public func serviceCoverageAndPricing(requestParameters: [String:Any] ,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/serviceCoverages/fetchMAA"
        callPostRequest(myurl: url, requestParameters: requestParameters,callback: callback)
        
    }
    public func specialistAvailableSlot(specialistId: String, serviceId: String,callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/specialists/available?serviceId="+serviceId+"&vendorUserId=" + specialistId + "&weeks=2"
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func getReasons( callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/reasons?specialities=26&slim=HIGH"
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
    public func appointmentDetailPage(requestParameters: [String: Any], callback : @escaping ((NSError?,String) -> Void))
    {
        let myurl =  apiRoot + "/resources/oauth/sso"
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
    public func getSpcialities( callback : @escaping ((NSError?,String) -> Void))
    {
        let url = apiRoot + "/resources/file/JSON/specialities.json"
        callGetRequest(myurl: url, callback: callback)
    }
   
    static public func gobackToParent()
    {
        SpriteHealthClient.parentCallback!(nil, "test string");
    }
    
    public func  callVPTFinder(callback : @escaping ((NSError?,String) -> Void)) {
        
       // let newViewController = (parentViewController.storyboard?.instantiateViewController(withIdentifier: "VPTFinderId"))!
        //guard let vc = UIStoryboard.load() as? VPTFinderViewController else {return}
        //parentViewController.present(newViewController, animated: true, completion: nil)
        
        SpriteHealthClient.parentCallback = callback;
        let newViewController = VPTFinderViewController()
        newViewController.modalPresentationStyle = .fullScreen
        SpriteHealthClient.rootController?.present(newViewController, animated:true,completion:nil)
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
    
}
