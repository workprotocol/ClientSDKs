//
//  VPTFinderViewController.swift
//  sdk
//
//  Created by Ashish Balhara on 28/06/21.
//

import UIKit

import CoreLocation


struct buttonViewModel {
    let primaryText : String
    let secondaryText: String
    let slotTimeText:String
    let imageurl: String
}

 class MultilineButton: UIButton {
        private let primaryLabel: UILabel = {
            let label = UILabel()
            label.numberOfLines=1
            label.textAlignment = .left;//.center
            label.textColor = .black
            label.font = UIFont.boldSystemFont(ofSize: 20)
            //label.font = .systemFont(ofSize: 20, weight: .semibold)
            return label
        }()
    private let secondaryLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines=1
        label.textAlignment = .left;//.center
        label.textColor = .darkGray
        label.font.withSize(20)
        //label.font = .systemFont(ofSize: 20, weight: .regular)
       
        return label
    }()
    private let slotTimeLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines=1
        label.textAlignment = .center
        label.textColor = .darkGray
        label.font.withSize(15)
        //label.font =   .systemFont(ofSize: 15, weight: .light)
        label.layer.borderWidth = 1
        label.layer.borderColor = UIColor.lightGray.cgColor
        label.layer.cornerRadius = 5
        return label
    }()
    
    private let specialistImage: UIImageView = {
        let image = UIImageView()
     
        return image
    }()
    
   
    

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(primaryLabel)
        addSubview(secondaryLabel)
        addSubview(slotTimeLabel)
        addSubview(specialistImage)
        clipsToBounds = true
        layer.cornerRadius = 8
        layer.borderWidth = 2
        layer.borderColor = UIColor.systemGray5.cgColor
        //backgroundColor = .systemBackground
        backgroundColor = .white
        //backgroundColor = UIColor.init(red: 100, green: 200, blue: 240, alpha: 1)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func configure(with viewModel:buttonViewModel)
    {
        primaryLabel.text = viewModel.primaryText
        secondaryLabel.text = viewModel.secondaryText
        slotTimeLabel.text = viewModel.slotTimeText
       

        let imagePath = viewModel.imageurl

        //let urlString = imagePath.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        //let url = URL(string: urlString)
        specialistImage.downloaded(from: imagePath)
        
        //var labelWidth = slotTimeLabel.intrinsicContentSize.width + 10
       // slotTimeLabel.frame = CGRect(x:slotTimeLabel.frame.origin.x , y: slotTimeLabel.frame.origin.y, width: labelWidth, height: slotTimeLabel.frame.height)
        
    }
    
 

    override func layoutSubviews() {
        super.layoutSubviews()
       
        primaryLabel.frame = CGRect(x: frame.size.width/4, y: 0, width: frame.size.width*3/4, height: frame.size.height*0.35)
        secondaryLabel.frame = CGRect(x: frame.size.width/4, y: frame.size.height*0.35, width: frame.size.width*3/4, height: frame.size.height*0.35)
        slotTimeLabel.frame = CGRect(x:frame.size.width/4 , y: frame.size.height*0.75, width: frame.size.width*0.7, height: frame.size.height*0.2)
        slotTimeLabel.layer.addWaghaBorder(edge: .top, color: UIColor.white, thickness: 1)
        
        let height = frame.size.height*3/4
        var width = frame.size.width/4
        var startX: CGFloat = 0.0
        if(height < width) {
            startX = (width-height)/2
            width = height
        }
        specialistImage.frame = CGRect(x:startX, y: 10, width: width , height: height)
        
        specialistImage.layer.borderWidth = 1
        specialistImage.layer.borderColor = UIColor.lightGray.cgColor
        specialistImage.layer.cornerRadius = specialistImage.bounds.width / 2
        specialistImage.clipsToBounds = true
    }
}



       
        


public class VPTFinderViewController: UIViewController, CLLocationManagerDelegate {
    
    @IBOutlet weak var navigationRightSpace: UILabel!
    @IBOutlet weak var navigationBackButton: UIButton!
    @IBOutlet weak var navigationLabel: UILabel!
    @IBOutlet weak var btnScrollView: UIScrollView!
    
    var specialistArr : [[String : String]] = [[ : ]]
    var specialities : [Int: String] = [Int: String]()
    
    var locationManager: CLLocationManager?
    var myActivityIndicator:UIActivityIndicatorView?
    public override func viewDidLoad() {
        super.viewDidLoad()
        
      
        if(SpriteHealthClient.primaryColor != "")
        {
            let primaryColor = SpriteHealthClient.hexStringToUIColor(hex:SpriteHealthClient.primaryColor)
            let primaryTextColor = SpriteHealthClient.hexStringToUIColor(hex:SpriteHealthClient.primaryTextColor)
            navigationLabel.backgroundColor = primaryColor
            navigationLabel.textColor = primaryTextColor
            navigationBackButton.backgroundColor = primaryColor
            navigationBackButton.tintColor = primaryTextColor
            navigationRightSpace.backgroundColor = primaryColor
            navigationRightSpace.textColor = primaryTextColor
           
        }
        // Do any additional setup after loading the view.
        myActivityIndicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                       
                       // Position Activity Indicator in the center of the main view
        myActivityIndicator!.center = view.center
               
                       // If needed, you can prevent Acivity Indicator from hiding when stopAnimating() is called
        myActivityIndicator!.hidesWhenStopped = true
                       
                       // Start Activity Indicator
        myActivityIndicator!.startAnimating()
        view.addSubview(myActivityIndicator!)
        
        locationManager = CLLocationManager()
        locationManager?.delegate = self
        
        locationManager?.requestWhenInUseAuthorization()
        
        
       
        
      
    }

    @IBAction func navigationBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
   
    public func displaySpecialists(state:String)
    {
        let cmObj = SpriteHealthClient.getInstance()
        
        cmObj.getSpecialities(){ [self] (error, results) in
            if let error = error {
                print(error)
            } else {
                    if(results != nil)
                    {
                        for specialty in results!{
                            specialities[specialty.value!] = specialty.name!
                        }
                    }
               
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
                dateFormatter.dateFormat = "MM/dd/yyyy h:mm a"
                let startDateTime = dateFormatter.string(from: currentDate)
                dateFormatter.dateFormat = "HH:mm:ss"
                let currentTime = dateFormatter.string(from: currentDate)
                var networkIds = ""
                if let planSubscriptions = SpriteHealthClient.memberInfo!.planSubscriptions  {
                    for planSubscription in planSubscriptions
                    {
                        if let planNetworks = planSubscription.networkIds
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
                var location = state;
                if(location != "")
                {
                    if let officeLocations = SpriteHealthClient.memberInfo!.officeLocations  {
                        for officeLocation in officeLocations
                        {
                            if let state = officeLocation.stateOrProvince
                            {
                                location = state
                                break;
                            }
                            
                        }
                        
                    }
                }
                
                let urlQueryParam: [String: Any] = [
                    "state" : location,
                    "specialities" : "26",
                    "serviceDefinitionIds" : 5414975176704000,
                    "startIndex" : 0,
                    "endIndex" : 10,
                    "getOnlyFirstAvailability" : true,
                    "networkIds" : networkIds,
                    "startDateTime" : startDateTime,
                    "currentTime" : currentTime
                ]
                
                cmObj.getAvailableSpecialists(queryParams:urlQueryParam){ (error, result) in
                    // do stuff with the result
                    if let error = error {
                        print(error)
                    } else {
                   

                        DispatchQueue.main.async {
                        self.dynamicButtonCreation(arrayJson: result)
                            myActivityIndicator!.stopAnimating()
                        }
                    }
                    
                   
                }
                
            }
        }
    }
    
    
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
      switch status {
      /*case .denied: // Setting option: Never
        print("LocationManager didChangeAuthorization denied")
      case .notDetermined: // Setting option: Ask Next Time
        print("LocationManager didChangeAuthorization notDetermined") */
      case .authorizedWhenInUse: // Setting option: While Using the App
        print("LocationManager didChangeAuthorization authorizedWhenInUse")
        
        // Stpe 6: Request a one-time location information
        locationManager?.requestLocation()
      case .authorizedAlways: // Setting option: Always
        print("LocationManager didChangeAuthorization authorizedAlways")
        
        // Stpe 6: Request a one-time location information
        locationManager?.requestLocation()
     /* case .restricted: // Restricted by parental control
        print("LocationManager didChangeAuthorization restricted") */
      default:
        print("LocationManager didChangeAuthorization")
       // displaySpecialists(state:"")
      }
    }

    // Step 7: Handle the location information
      public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
      print("LocationManager didUpdateLocations: numberOfLocation: \(locations.count)")
      let dateFormatter = DateFormatter()
      dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
      
      locations.forEach { (location) in
        print("LocationManager didUpdateLocations: \(dateFormatter.string(from: location.timestamp)); \(location.coordinate.latitude), \(location.coordinate.longitude)")
        let location = CLLocation(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude)
        let geocoder = CLGeocoder()

           print("-> Finding user address...")
       
        geocoder.reverseGeocodeLocation(location, completionHandler: { [self](placemarks, error) in
            if (error != nil)
            {
                print("reverse geodcode fail: \(error!.localizedDescription)")
                displaySpecialists(state:"")
            }
               var placemark:CLPlacemark!

            if error == nil && placemarks!.count > 0 {
                placemark = placemarks![0] as CLPlacemark
                   var addressString : String = ""
                       if placemark.administrativeArea != nil {
                        addressString = placemark.administrativeArea!
                       }
                      
                   print(addressString)
                    displaySpecialists(state: addressString)
               }
           })
      }
    }
    
    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
      print("LocationManager didFailWithError \(error.localizedDescription)")
      if let error = error as? CLError, error.code == .denied {
         // Location updates are not authorized.
        // To prevent forever looping of `didFailWithError` callback
         locationManager?.stopMonitoringSignificantLocationChanges()
        displaySpecialists(state:"")
         return
      }
    }
    
    
    @IBAction func backBtnClick(_ sender: Any) {
        print("buttonClick")
        /*let newViewController = SpecialistDetailViewController()
        newViewController.modalPresentationStyle = .fullScreen
        self.present(newViewController, animated:true,completion:nil) */
    }
    
    public init() {
        super.init(nibName: "VPTFinderViewController", bundle: Bundle(for: VPTFinderViewController.self))
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    @objc func scrollButtonAction(sender: UIButton) {
            //print("Hello \(sender.tag) is Selected")
        //print(specialistArr[sender.tag]["name"])
        //let newViewController = SpecialistDetailViewController(id:specialistArr[sender.tag]["id"]!,name: specialistArr[sender.tag]["name"]!)
        let newViewController = SpecialistDetailViewController(specialistArr:specialistArr[sender.tag])
        newViewController.modalPresentationStyle = .fullScreen
        self.present(newViewController, animated:true,completion:nil)
        }
    
  
            
        
        func dynamicButtonCreation(arrayJson: [Specialist]?) {
         
            
            
            btnScrollView.isScrollEnabled = true
            btnScrollView.isUserInteractionEnabled = true
            var count = 0
            let screenSize: CGRect = UIScreen.main.bounds
        
            for json in arrayJson! {
                  var specialistName = ""
                var specialistId = ""
                var specialistAvailableSlot = ""
                var specialistImageUrl = "";
                var speciality = "" //Physical Therapist
                //var idInt:Int64 = 0;
                if json.name != nil {
                    specialistName = json.name!;
                      }
                    specialistId = String(json.id)
                    
                if let specializations = json.specialization {
                        for specialization in specializations
                        {
                            if(speciality  == "")
                            {
                                speciality = specialities[specialization]!
                            }
                            else {
                                speciality = speciality + ", " + specialities[specialization]!
                            }
                            
                        }
                  
                    }
                
                if  json.imageIds != nil {
                    if(json.imageIds!.count>0)
                    {
                        specialistImageUrl = "https://wpbackendqa.appspot.com/resources/images/" + String(json.imageIds![0])
                    }
                  
                    }
                if  json.availableSlots != nil {
                    //specialistAvailableSlots = availableSlots
                    let availableSlots = json.availableSlots!
                    if(availableSlots.count == 0)
                    {
                        specialistAvailableSlot = "No Availability"
                    }
                    else
                    {
                            for slot in availableSlots {
                                if slot.startTime != nil{
                                  //var startTime1 = startTime
                                    let dateFormatter = DateFormatter()
                                            dateFormatter.dateFormat = "MM-dd-yyyy HH:mm:ss"
                                            let date = dateFormatter.date(from: slot.startTime!)
                                            dateFormatter.dateFormat = "MMM dd, yyyy hh:mm aaa"
                                    specialistAvailableSlot = dateFormatter.string(from: date!)
                                    
                                    break;
                                    
                                    }
                            }
                        }
                }
                else {
                    specialistAvailableSlot = "No Availability"
                }
                var serviceIdGlobal:String = "0"
                if json.serviceId != nil {
                  
                    serviceIdGlobal =  String(json.serviceId!)
                  
                    }
                var vendorIdGlobal:String = "0"
                if json.vendorId != nil  {
                  
                    vendorIdGlobal =  String(json.vendorId!)
                  
                    }
                
                
                specialistArr.append([
                    "id": specialistId,
                    "name": specialistName,
                    "imageUrl": specialistImageUrl,
                    "serviceId" : serviceIdGlobal,
                    "vendorId" : vendorIdGlobal,
                    "speciality" : speciality
                ]);
                let xTopLeft = 0
                
                let topmargin = 0
                
                let yTopLeft = topmargin + count*120
                
               
                let button = MultilineButton(frame: CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width-30), height: 100))
               // button.center = view.center
                button.configure(with: buttonViewModel(primaryText: specialistName, secondaryText: speciality, slotTimeText: specialistAvailableSlot, imageurl : specialistImageUrl))
                button.tag = count+1
                button.addTarget(self, action: #selector(self.scrollButtonAction), for: .touchUpInside)
               
                btnScrollView.addSubview(button)
                count += 1
            }

        
    
            
            let height = count*120
        
            btnScrollView.contentSize = CGSize(width: Int(btnScrollView.frame.size.width), height: height)
            
        }
  
   

}


extension UIImageView {
    func downloaded(from url: URL, contentMode mode: UIView.ContentMode = .scaleAspectFit) {  // for swift 4.2 syntax just use ===> mode: UIView.ContentMode
        contentMode = mode
        URLSession.shared.dataTask(with: url) { data, response, error in
            guard
                let httpURLResponse = response as? HTTPURLResponse, httpURLResponse.statusCode == 200,
                let mimeType = response?.mimeType, mimeType.hasPrefix("image"),
                let data = data, error == nil,
                let image = UIImage(data: data)
                else { return }
            DispatchQueue.main.async() {
                self.image = image
            }
        }.resume()
    }
    func downloaded(from link: String, contentMode mode: UIView.ContentMode = .scaleAspectFit) {  // for swift 4.2 syntax just use ===> mode: UIView.ContentMode
        guard let url = URL(string: link) else { return }
        downloaded(from: url, contentMode: mode)
    }
}


extension CALayer {
    func addWaghaBorder(edge: UIRectEdge, color: UIColor, thickness: CGFloat) {
    let border = CALayer()
    switch edge {
        case UIRectEdge.top:
        border.frame = CGRect(x: 0, y: 0, width: self.frame.width, height: 1)
        break
        case UIRectEdge.bottom:
        border.frame = CGRect(x: 0, y: self.frame.height - 1, width: self.frame.width, height: 1)
        break
        case UIRectEdge.left:
        border.frame = CGRect(x: 0, y: 0, width: 1, height: self.frame.height)
        break
        case UIRectEdge.right:
        border.frame = CGRect(x: self.frame.width - 1, y: 0, width: 1, height: self.frame.height)
        break
        default:
        break
    }
    border.backgroundColor = color.cgColor;
    self.addSublayer(border)
    }
    
    
    
}


