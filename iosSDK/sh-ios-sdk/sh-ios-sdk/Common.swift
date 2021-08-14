//
//  Common.swift
//  sdk
//
//  Created by Ashish Balhara on 28/06/21.
//

import Foundation
import UIKit
public class SpriteHealthClient {
    static var rootController: UIViewController?
    static var parentCallback:((String) -> Void)?
    static var user_identity = "";
    static var client_id = "";
    static var token_expires_in=1800;
    static var lastAccessTime = Date().timeIntervalSinceReferenceDate;
     public init(controller: UIViewController) {
        SpriteHealthClient.rootController = controller
     }
    // /resources/oauth/authorize?response_type=token&client_id=0b5c8d72f9794ec69870886cd060bc82&no_redirect=true&skip_user_auth=true&user_identity=dag@berger.com
    private let apiRoot = "https://oauth-dot-wpbackendqa.appspot.com"
    private  var auth_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.bUJjUm1xREJ0LzAzQ2hKSWh2by9jRnA4V0dsRkRnQ1c2cDFlNndvQ2xUbmZPajZSRW9aYkNSZmIrWlNVT1ZnMVBRcDVCRVlERDlpWHFiTlR4M2M3TjkvS1ZEUm8yeFQ1WTJrVGxmNjFHditDUUNXSFdkMDZBRElPYnpsZGJHTG9tb1lxYlVBaUMyMTlaRjJCNSsvQ1c5TUhhaFhJcllRM1J4ZEZtT0s0cHF0WElDVUJHc0tOL1lLR0pML1pKdFZBUlRlREVaaUprQVpWaHlUT21kQnZiejhvYzNGKytVQnlucHJodGh3SlY4dFAyWkh6a3lJYk5OOTVmMysrWnJTK1ZKa1NsMVpsS2tndWU1NkNCMmFySkFsalQyWkErS1gvbUpEMmdHUjhMNkdvY1UyOXBCd3RJeTNHMS9uOUlNN0hWSURJWmpJdk5CNnYzNjJVM0gwREtVNzdmSmhSTUZ4ZmRoc3B4QkR1b0VWNCt0bGVUNE9QN251Z2NwWjhSV0hSK2pDOGZWc2ZXZ2x6bENQQTVnUTBmZzJLNWpoaVdLMkVPYUpweHIyMDllKy9vaUFJV3U0WUpYeHhrbk5GbTJ4Rk5xbnEzSm5NS0VCbmo3UXRIYlpGaXNnbVdidS9OQnFTTUFwalBXUVFnMkVXT2RGeGMwdGhaTFpaOEZIRUpGUXVJU3AyOHJ4VkVSZkVXbEEvZ2U1RWlwVGcwZzBzZlVSVg.ARVDXusPZ4jkhC35sVior7zGzNlvePVis/bvpDZLo1M="
    public init() {}
    
    //let myurl = "https://wpbackendqa.appspot.com/resources/specialists/available?specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=10&startDateTime=06/21/2021 12:18 pm&currentTime=14:18:39&getOnlyFirstAvailability=true&networkIds=5783379589988352,6214613415755776,6206424134713344,6293602384740352,6306161213046784,6206424134713344,5783379589988352,6293602384740352"
    
    public func setToken(authToken: String)
    {
        auth_token = authToken
    }
    public func getToken() -> String
    {
        return auth_token
    }
    
    
    private func callGetRequest(myurl:String, callback : @escaping ((String) -> Void))
    {
        let url = URL(string: myurl)
        guard let requestUrl = url else { fatalError() }
        // Create URL Request
        var request = URLRequest(url: requestUrl)
        let headers = [
            "auth-token" : auth_token
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
                callback(dataString)
            }
            
        }
        task.resume()
    }
    private func stringFromAny(_ value:Any?) -> String {

        if let nonNil = value, !(nonNil is NSNull) {

            return String(describing: nonNil) // "Optional(12)"
        }
        return ""
    }
    
    private func callPostRequest(myurl:String, requestParameters: [String: Any] ,callback : @escaping ((String) -> Void))
    {
        let url = URL(string: myurl)
        guard let requestUrl = url else { fatalError() }
        // Create URL Request
        var request = URLRequest(url: requestUrl)
        let headers = [
            "auth-token" : auth_token,
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
                callback(dataString)
            }
            
        }
        task.resume()
    }
    
    public func memberDetail (callback : @escaping ((String) -> Void))
    {
        let url = apiRoot + "/resources/user?withCoverage=true";
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func familyMembers(familyId: String ,callback : @escaping ((String) -> Void))
    {
        let url = apiRoot + "resources/user/family/members?familyId=" + familyId;
        callGetRequest(myurl: url, callback: callback)
    }
    public func specialistAvailable(callback : @escaping ((String) -> Void))
    {
        let url = "https://wpbackendqa.appspot.com/resources/specialists/available?specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=10&getOnlyFirstAvailability=true&networkIds=5783379589988352,6214613415755776,6206424134713344,6293602384740352,6306161213046784,6206424134713344,5783379589988352,6293602384740352"
        
        callGetRequest(myurl: url, callback: callback)
    }
    public func specialistDetail(specialistId: String, callback : @escaping ((String) -> Void))
    {
        let url = "https://wpbackendqa.appspot.com/resources/user/specialists/" + specialistId + "?slim=HIGH";
        
        
        callGetRequest(myurl: url, callback: callback)
    }
    public func reasonList(callback : @escaping ((String) -> Void))
    {
        let url = apiRoot + "/resources/reasons?specialities=26"
        callGetRequest(myurl: url, callback: callback)
    }
    public func serviceCoverageAndPricing(requestParameters: [String:Any] ,callback : @escaping ((String) -> Void))
    {
        let url = apiRoot + "/resources/serviceCoverages/fetchMAA"
        callPostRequest(myurl: url, requestParameters: requestParameters,callback: callback)
        
    }
    public func specialistAvailableSlot(specialistId: String, callback : @escaping ((String) -> Void))
    {
        let url = apiRoot + "/resources/specialists/available?serviceId=6207822929854464&vendorUserId=" + specialistId + "&weeks=1"
        callGetRequest(myurl: url, callback: callback)
    }
    public func listOfServiceByReason(reasonId: String, specialistId: String,callback : @escaping ((String) -> Void))
    {
        let url = apiRoot + "/resources/reasons/" + reasonId + "/services?vendorId=" + specialistId;
        callGetRequest(myurl: url, callback: callback)
    }
    
    public func appointmentBooking(requestParameters: [String: Any], callback : @escaping ((String) -> Void))
    {
        let myurl =  "https://wpbackendqa.appspot.com/resources/calendar/event"
        if (!JSONSerialization.isValidJSONObject(requestParameters)) {
               print("is not a valid json object")
               return
           }
        callPostRequest(myurl: myurl, requestParameters: requestParameters,callback: callback)
        
    }
    
    static public func gobackToParent()
    {
        SpriteHealthClient.parentCallback!("test string");
    }
    
    public func  callVPTFinder(callback : @escaping ((String) -> Void)) {
        
       // let newViewController = (parentViewController.storyboard?.instantiateViewController(withIdentifier: "VPTFinderId"))!
        //guard let vc = UIStoryboard.load() as? VPTFinderViewController else {return}
        //parentViewController.present(newViewController, animated: true, completion: nil)
        
        SpriteHealthClient.parentCallback = callback;
        let newViewController = VPTFinderViewController()
        newViewController.modalPresentationStyle = .fullScreen
        SpriteHealthClient.rootController?.present(newViewController, animated:true,completion:nil)
    }
    
    
}
