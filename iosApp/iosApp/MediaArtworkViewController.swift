import UIKit
import MediaPlayer
import SwiftUI
import ComposeApp

final class MediaArtworkViewControllerFactoryImpl: MediaArtworkViewControllerFactory {
    func createMediaArtworkViewController(persistentID: Int64) -> UIViewController {
        MediaArtworkViewController.init(persistentId: persistentID)
    }
}

final class MediaArtworkViewController: UIViewController {

    private let persistentId: Int64
    private let imageView = UIImageView()

    init(persistentId: Int64) {
        self.persistentId = persistentId
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadArtwork()
    }

    private func setupUI() {
        view.backgroundColor = .black
        imageView.contentMode = .scaleAspectFit
        imageView.translatesAutoresizingMaskIntoConstraints = false

        view.addSubview(imageView)
        NSLayoutConstraint.activate([
            imageView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            imageView.topAnchor.constraint(equalTo: view.topAnchor),
            imageView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
    
    private func loadArtwork() {
        let predicate = MPMediaPropertyPredicate(
            value: NSNumber(value: persistentId),
            forProperty: MPMediaItemPropertyPersistentID
        )

        let query = MPMediaQuery.songs()
        query.addFilterPredicate(predicate)

        guard let item = query.items?.first else {
            print("Media item not found for id: \(persistentId)")
            return
        }

        loadArtwork(from: item)
    }
    
    private func loadArtwork(from item: MPMediaItem) {
        guard let artwork = item.artwork else {
            imageView.image = UIImage(systemName: "music.note")
            return
        }

        let size = CGSize(width: 600, height: 600)
        imageView.image = artwork.image(at: size)
    }

}
