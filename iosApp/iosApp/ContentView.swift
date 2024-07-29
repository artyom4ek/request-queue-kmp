import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = RequestQueueViewModel()
	var body: some View {
        RequestQueueScreen(
            isConnectionAvailable: $viewModel.isConnectionAvailable,
            highPriorityQueueSize: $viewModel.highPriorityQueueSize,
            lowPriorityQueueSize: $viewModel.lowPriorityQueueSize,
            errorMessage: $viewModel.errorMessage,
            onAddHighPriorityRequest: {
                viewModel.addHighPriorityRequest()
            },
            onAddLowPriorityRequest: {
                viewModel.addLowPriorityRequest()
            }
        )
	}
}

struct RequestQueueScreen: View {
    @Binding var isConnectionAvailable: Bool
    @Binding var highPriorityQueueSize: Int
    @Binding var lowPriorityQueueSize: Int
    @Binding var errorMessage: String?

    var onAddHighPriorityRequest: () -> Void
    var onAddLowPriorityRequest: () -> Void

    var body: some View {
        VStack {
            Button(action: {
                onAddHighPriorityRequest()
            }) {
                Text("Add High Priority Request")
                    .padding()
            }

            Text("High Priority Queue Size: \(highPriorityQueueSize)")
                .padding()

            Spacer().frame(height: 32)

            Button(action: {
                onAddLowPriorityRequest()
            }) {
                Text("Add Low Priority Request")
                    .padding()
            }

            Text("Low Priority Queue Size: \(lowPriorityQueueSize)")
                .padding()

            Spacer().frame(height: 32)

            Text("Is connected: \(isConnectionAvailable ? "Yes" : "No")")
                .padding()
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}