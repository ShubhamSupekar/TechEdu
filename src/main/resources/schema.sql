CREATE TABLE IF NOT EXISTS user (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    username VARCHAR(250) NOT NULL,
                                    password VARCHAR(250) NOT NULL
);
CREATE TABLE IF NOT EXISTS videos (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      title VARCHAR(255),
    description TEXT,
    file_path VARCHAR(255),
    uploaded_by INT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by) REFERENCES user(id)
    );
