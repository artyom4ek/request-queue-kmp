import Foundation
import shared
import Combine

class RequestQueueViewModel: ObservableObject {
    @Published var highPriorityQueueSize: Int = 0
    @Published var lowPriorityQueueSize: Int = 0
    @Published var isConnectionAvailable: Bool = true
    @Published var errorMessage: String? = nil

    private let requestQueueRepository: RequestQueueRepository

     init() {
        self.requestQueueRepository = KoinHelper().getRequestQueueRepository()

        self.isConnectionAvailable = requestQueueRepository.getConnectionStatus()

            // Set callbacks
            requestQueueRepository.setOnQueueSizeChangedCallback { size, priority in
                DispatchQueue.main.async {
                let sizeInt = size as? KotlinInt
                    if priority == .high {
                        self.highPriorityQueueSize = size.intValue
                    } else {
                        self.lowPriorityQueueSize = size.intValue
                    }
                }
            }

            requestQueueRepository.setOnNetworkErrorCallback { message in
                DispatchQueue.main.async {
                    self.errorMessage = message

                    // Hide error message after 4 seconds
                    DispatchQueue.main.asyncAfter(deadline: .now() + 4) {
                        self.errorMessage = nil
                    }
                }
            }

            requestQueueRepository.setOnConnectionChangedCallback { status in
                DispatchQueue.main.async {
                    self.isConnectionAvailable = status as! Bool
                }
            }
     }

    func addHighPriorityRequest() {
        requestQueueRepository.addRequest(url: "https://day.io", queuePriority: .high)
    }

    func addLowPriorityRequest() {
        requestQueueRepository.addRequest(url: "https://google.com", queuePriority: .low)
    }
}
