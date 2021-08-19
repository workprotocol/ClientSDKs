//
//  VPTFinderViewController.swift
//  sdk
//
//  Created by Ashish Balhara on 28/06/21.
//

import UIKit

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



       
        


public class VPTFinderViewController: UIViewController {
    
    @IBOutlet weak var navigationRightSpace: UILabel!
    @IBOutlet weak var navigationBackButton: UIButton!
    @IBOutlet weak var navigationLabel: UILabel!
    @IBOutlet weak var btnScrollView: UIScrollView!
    
    var specialistArr : [[String : String]] = [[ : ]]
    var specialities : [Int: String] = [Int: String]()
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
        let myActivityIndicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                       
                       // Position Activity Indicator in the center of the main view
                       myActivityIndicator.center = view.center
               
                       // If needed, you can prevent Acivity Indicator from hiding when stopAnimating() is called
                       myActivityIndicator.hidesWhenStopped = true
                       
                       // Start Activity Indicator
                       myActivityIndicator.startAnimating()
               view.addSubview(myActivityIndicator)
        
        let cmObj = SpriteHealthClient()
        
        cmObj.fetchSpecialities(){ [self] (error, result) in
            if let error = error {
                print(error)
            } else {
            print(result)
                let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                if let list = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [AnyObject] {
                    for json in list {
                        specialities[json["value"] as! Int] = json["name"] as? String
                    }
                    
                }
                cmObj.specialistAvailable(){ (error, result) in
                    // do stuff with the result
                    if let error = error {
                        print(error)
                    } else {
                    print(result)
                    //JSONArray jsonArray = jsnobject.getJSONArray(result);
                    let resultConverted = result.replacingOccurrences(of: "\\", with: "")
                    if let list = SpriteHealthClient.convertToDictionary(text: resultConverted) as? [AnyObject] {

                       print(list);
                        print(type(of: list))
                        DispatchQueue.main.async {
                        self.dynamicButtonCreation(arrayJson: list)
                            myActivityIndicator.stopAnimating()
                        }
                    }
                    }
                   
                }
                
            }
        }
       
        
       // btnScrollView.contentSize = CGSize(width: btnScrollView.contentSize.width, height: 2000)
        btnScrollView.isUserInteractionEnabled = true
       // backBtn.superview?.bringSubviewToFront(backBtn);
        
      
        //self.navigationController!.navigationBar.tintColor = 
    }

    @IBAction func navigationBack(_ sender: Any) {
        self.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    /*func convertToDictionary(text: String) -> Any? {

     if let data = text.data(using: .utf8) {
         do {
            // return try JSONSerialization.jsonObject(with: data, options: []) as? Any
         
            let json = try? JSONSerialization.jsonObject(with: data as Data, options: []) as? Any
            return json
         } catch {
             print(error.localizedDescription)
         }
     }

     return nil
    }*/
    
    
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
            print("Hello \(sender.tag) is Selected")
        //print(specialistArr[sender.tag]["name"])
        //let newViewController = SpecialistDetailViewController(id:specialistArr[sender.tag]["id"]!,name: specialistArr[sender.tag]["name"]!)
        let newViewController = SpecialistDetailViewController(specialistArr:specialistArr[sender.tag])
        newViewController.modalPresentationStyle = .fullScreen
        self.present(newViewController, animated:true,completion:nil)
        }
    
  
            
        
        func dynamicButtonCreation(arrayJson: Array<AnyObject>) {
         
            
            
            btnScrollView.isScrollEnabled = true
            btnScrollView.isUserInteractionEnabled = true
            var count = 0
            let screenSize: CGRect = UIScreen.main.bounds
            for json in arrayJson {
                  var specialistName = ""
                var specialistId = ""
                var specialistAvailableSlot = ""
                var specialistImageUrl = "";
                var speciality = "" //Physical Therapist
                //var idInt:Int64 = 0;
                  if let name = json["name"] as? String {
                    specialistName = name;
                      }
                  if let id = json["id"] as? Int64 {
                    specialistId = String(id)
                    //idInt = id
                    
                      }
                if let specializations = json["specialization"] as? [Int] {
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
                
                if let imageIds = json["imageIds"] as? [Int64] {
                  
                    specialistImageUrl = "https://wpbackendqa.appspot.com/resources/images/" + String(imageIds[0])
                  
                    }
                if let availableSlots = json["availableSlots"] as? [NSDictionary] {
                    //specialistAvailableSlots = availableSlots
                    if(availableSlots.isEmpty)
                    {
                        specialistAvailableSlot = "No Availability"
                    }
                    else
                    {
                            for slot in availableSlots {
                                if let startTime = slot["startTime"] as? String {
                                  //var startTime1 = startTime
                                    let dateFormatter = DateFormatter()
                                            dateFormatter.dateFormat = "MM-dd-yyyy HH:mm:ss"
                                            let date = dateFormatter.date(from: startTime)
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
                if let serviceId = json["serviceId"] as? Int64 {
                  
                    serviceIdGlobal =  String(serviceId)
                  
                    }
                var vendorIdGlobal:String = "0"
                if let vendorId = json["vendorId"] as? Int64 {
                  
                    vendorIdGlobal =  String(vendorId)
                  
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
                /*
                let button = UIButton()
                button.tag = count
                button.frame = CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width), height: 45)
                button.backgroundColor = UIColor.black
                button.setTitle(specialistName, for: .normal)
                button.addTarget(self, action: #selector(self.scrollButtonAction), for: .touchUpInside)
                */
                btnScrollView.addSubview(button)
                count += 1
            }

        
     /*   btnScrollView.isScrollEnabled = true
        btnScrollView.isUserInteractionEnabled = true
            
            let numberOfButtons = 16
           
            var count = 0
        let screenSize: CGRect = UIScreen.main.bounds
            
            for _ in 1...numberOfButtons {
                
                var xTopLeft = 0
                var yTopLeft = 100 + count*50
                
                let Button = UIButton()
                Button.tag = count
                Button.frame = CGRect(x: xTopLeft, y: yTopLeft, width: Int(screenSize.width), height: 45)
                Button.backgroundColor = UIColor.black
                Button.setTitle("Hello \(count) ", for: .normal)
                Button.addTarget(self, action: #selector(self.scrollButtonAction), for: .touchUpInside)
                btnScrollView.addSubview(Button)
                count += 1
               
            }
            */
            
            let height = count*120
        
            btnScrollView.contentSize = CGSize(width: Int(btnScrollView.frame.size.width), height: height)
            
        }
  
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

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
