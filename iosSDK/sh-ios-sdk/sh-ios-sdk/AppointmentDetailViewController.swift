//
//  AppointmentDetailViewController.swift
//  sdk
//
//  Created by Ashish Balhara on 04/07/21.
//

import Foundation

import UIKit

import WebKit
import SafariServices
class AppointmentDetailViewController: UIViewController, SFSafariViewControllerDelegate {
    @IBOutlet weak var webview: WKWebView!
    
    
    
    public init() {
        super.init(nibName: "AppointmentDetailViewController", bundle: Bundle(for: AppointmentDetailViewController.self))
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    var InfoArr : [String : String]?
    public init(specialistArr : [String : String]) {
        InfoArr = specialistArr;
        super.init(nibName: "AppointmentDetailViewController", bundle: Bundle(for: AppointmentDetailViewController.self))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //lblCounter.superview?.bringSubviewToFront(lblCounter)
        
        let shObject = SpriteHealthClient()   // Constructor
        var authToken =  shObject.getToken()
        
        let appointmentId = InfoArr?["appointmentId"]
        
        
        
        
        var urlString: String = "https://wpfrontendqa.appspot.com/globals/appointments/<appointmentId>?#access_token=<access_token>"
        authToken = authToken.replacingOccurrences(of: "Bearer ", with: "")
        urlString = urlString.replacingOccurrences(of: "<access_token>", with: authToken)
        urlString = urlString.replacingOccurrences(of: "<appointmentId>", with: appointmentId!)
         urlString = "https://wpfrontendqa.appspot.com/globals/appointments/6199056494755840?#access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkYWdAYmVyZ2VyLmNvbSIsImNsaWVudElkIjoiMGI1YzhkNzJmOTc5NGVjNjk4NzA4ODZjZDA2MGJjODIiLCJyb2xlIjoiQ0UiLCJhdXRoR3JhbnRUeXBlIjoiaW1wbGljaXQiLCJpc3MiOiJTcHJpdGUgSGVhbHRoIEFQSXMiLCJ0aW1lWm9uZSI6IkFtZXJpY2FcL0NoaWNhZ28iLCJ1c2VyQWdlbnQiOiJzZGtUZXN0XC82IENGTmV0d29ya1wvMTIyMC4xIERhcndpblwvMjAuMy4wIiwidXNlcklkIjo2MjgyNzE3MzgzNjIyNjU2LCJhdWQiOiIwYjVjOGQ3MmY5Nzk0ZWM2OTg3MDg4NmNkMDYwYmM4MiIsIm5iZiI6MTYyODc3NTE5NSwic2NvcGUiOiJmdWxsX2FjY2VzcyIsImlzU1NPIjp0cnVlLCJleHAiOjE2Mjg4NjE1OTUsImF1dGhUeXBlIjoiTkFUSVZFIiwiaWF0IjoxNjI4Nzc1MTk1LCJqdGkiOiJmNTE0MDc5Ny1jMWRmLTRkNjgtOTRkZC0wM2Y5NjZmM2E4N2MiLCJlbWFpbCI6ImRhZ0BiZXJnZXIuY29tIn0.BEUGyZg-wLLV6SDwczIOiSybH0_tuDhuE71_wo8Bo9OP3mGPH1h4oKCApA-oUtqCpk8R8G7uNPh64BO4qy-l02Z-DkEH4S55wDvxwPrEbOMlYqwnuIslmewOGFww7y9OviYw2FJMfozk0jYgkteD7vUqNfgLOQwPbjGb-kkWIImwqmrkmQSez4dWmAEPEyLDfUBPHk7Wt1FFaIGhyk9nUHQNXd5JurbH1eCFesK47euTf8Ig8vaAmbIUjGcOGdtta0xvF2Z_T9RmAOySi6P6bJII3CB11MGeY6nWwX9DqyYzzQuehzvcoLvMAv-vtty7zHULZQayT6m3fA-sz_JtpA"
        let url = URL(string: urlString)
        
        if let url = URL(string: urlString) {
            let vc = SFSafariViewController(url: url, entersReaderIfAvailable: true)
            vc.delegate = self
           
            present(vc, animated: true)
        }
        return;
        
        
        webview.configuration.processPool.perform(Selector(("_setCookieAcceptPolicy:")), with: HTTPCookie.AcceptPolicy.always)
        /*
        var htmlString = """
        <html>
        <head>

        <script type="text/javascript">
            window.onload=function(){
                
                    document.getElementById("ssoMessage").style="display:block";
                    var form=document.getElementById("ssoForm");
                    form.submit();

            }
            </script>
          
        </head>

        <body>
         
          <div id="ssoMessage" >
            <h3>loading... </h3>
        </div>

          <form method="post" id="ssoForm" action="https://wpbackendqa.appspot.com/resources/oauth/sso">
              <input type="hidden" id="accessToken" name="accessToken" value="##authToken##">
              
        <input type="hidden" id="ssoLandingURL" name="ssoLandingURL" value="https://wpfrontendqa.appspot.com/globals/appointments/##appointmentId##">
                        
            </form>
            
            </body>
            
            </html>
        """
        
        htmlString = htmlString.replacingOccurrences(of: "##authToken##", with: authToken)
        htmlString = htmlString.replacingOccurrences(of: "##appointmentId##", with: appointmentId!)
        webview.loadHTMLString(htmlString, baseURL: nil)
        */
       /*
        var urlString: String = "https://wpfrontendqa.appspot.com/globals/appointments/<appointmentId>?#access_token=<access_token>"
        authToken = authToken.replacingOccurrences(of: "Bearer ", with: "")
        urlString = urlString.replacingOccurrences(of: "<access_token>", with: authToken)
        urlString = urlString.replacingOccurrences(of: "<appointmentId>", with: appointmentId!)
         */
       // urlString = urlString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        
       /* urlString = "https://berger.spritehealth.com/globals/appointments/4620834711797760?#access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkYWdAYmVyZ2VyLmNvbSIsImNsaWVudElkIjoiMGI1YzhkNzJmOTc5NGVjNjk4NzA4ODZjZDA2MGJjODIiLCJyb2xlIjoiQ0UiLCJhdXRoR3JhbnRUeXBlIjoiaW1wbGljaXQiLCJpc3MiOiJodHRwczpcL1wvb2F1dGgtZG90LXdwYmFja2VuZHFhLmFwcHNwb3QuY29tXC9yZXNvdXJjZXNcL29hdXRoXC9hdXRoZW50aWNhdGUiLCJ0aW1lWm9uZSI6IkFtZXJpY2FcL0NoaWNhZ28iLCJ1c2VyQWdlbnQiOiJNb3ppbGxhXC81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXRcLzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZVwvOTIuMC40NTE1LjEzMSBTYWZhcmlcLzUzNy4zNiIsInVzZXJJZCI6NjI4MjcxNzM4MzYyMjY1NiwiYXVkIjoiaHR0cHM6XC9cL29hdXRoLWRvdC13cGJhY2tlbmRxYS5hcHBzcG90LmNvbSIsIm5iZiI6MTYyODc3MDg1NSwic2NvcGUiOiJ1c2VyX3JlYWQgYXBwb2ludG1lbnRfcmVhZCBhcHBvaW50bWVudF93cml0ZSIsImlzU1NPIjpmYWxzZSwiZXhwIjoxNjI4ODU3MjU1LCJhdXRoVHlwZSI6Ik5BVElWRSIsImlhdCI6MTYyODc3MDg1NSwianRpIjoiM2Q2MmNhZWMtOTc1ZC00ODlkLTljZGItNzE3NzY0MGYyY2VhIiwiZW1haWwiOiJkYWdAYmVyZ2VyLmNvbSJ9.lhKN2EJ58z7weoeDCxDZG5EqQ3t_Xn9IqNxnhAVgnRkPQ50Dba6ghZw-cWcunrWD0cK_cHJ2nIPpIbJOV85WGaPGW4iuS1LR1A63dsVu1W9ZN6wR667QA3HPTTj0V2N-QLNRNoo9sxm3OUUr_CGbohI-M-NoDwqQakcNg9nPBMgKipwAKsG8INFcnFIfd-Jxp3L2q-i-GlL8wAIht7PMzCix5CbTVHX7AarqTh55_kDnteQ90gzntUAwL6fjJWe6Afo7Oey8XxvFncF6fK12PwqHey6qezUkbKcJbKOxRNWCB9FFH2WiJ2SFcCmiMLOdySaXlWAy5loRa7wkTGwGUA"
 */
        /*
        print(urlString)
        let url = URL(string: urlString)
        webview.load(URLRequest(url: url!))
         */
        
    }
    @IBAction func navigationBack(_ sender: Any) {
        SpriteHealthClient.gobackToParent();
        
          
        self.presentingViewController?.presentingViewController?.presentingViewController?.presentingViewController?.presentingViewController?.dismiss(animated: true, completion: nil)
        
    }
    
    func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        dismiss(animated: true)
        SpriteHealthClient.gobackToParent();
        
          
        self.presentingViewController?.presentingViewController?.presentingViewController?.presentingViewController?.presentingViewController?.dismiss(animated: true, completion: nil)
    }
   
}


/*
 extension AppointmentDetailViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
      guard let response = navigationResponse.response as? HTTPURLResponse,
        let url = navigationResponse.response.url else {
        decisionHandler(.cancel)
        return
      }

      if let headerFields = response.allHeaderFields as? [String: String] {
        let cookies = HTTPCookie.cookies(withResponseHeaderFields: headerFields, for: url)
        cookies.forEach { cookie in
          webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
        }
      }
      
      decisionHandler(.allow)
    }
}*/
