import mysql.connector

# --- Step 1: Connect to MySQL ---
connection = mysql.connector.connect(
    host="localhost",        # change if using remote host
    user="root",             # your MySQL username
    password="bihari",       # your MySQL password
    database="medicines_db"  # your database name
)

cursor = connection.cursor()

# First, create the table if it doesn't exist
create_table_sql = """
CREATE TABLE IF NOT EXISTS medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(255),
    generic_substitute VARCHAR(255),
    dosage VARCHAR(50),
    brand VARCHAR(255),
    price DECIMAL(10,2),
    salt_name VARCHAR(255),
    availability VARCHAR(50)
)
"""
cursor.execute(create_table_sql)

# --- Step 2: Read and process data from text file ---
with open("data.txt", "r") as file:
    for line in file:
        line = line.strip()
        if not line or line.startswith('/*') or line.endswith('*/'):  # Skip empty lines and comments
            continue
        
        # Remove parentheses and split by comma
        line = line.strip('(').strip(')').strip(',')
        values = [val.strip().strip("'").strip('"') for val in line.split(',')]
        
        # Debug print
        print(f"Number of values: {len(values)}")
        print(f"Values: {values}")
        
        if len(values) < 7:  # Skip incomplete lines
            continue

        # Ensure we only take the first 7 values and validate price
        try:
            # Extract and validate price (5th element, index 4)
            price = float(values[4])
            # Create tuple with validated values
            validated_values = (
                values[0],  # medicine_name
                values[1],  # generic_substitute
                values[2],  # dosage
                values[3],  # brand
                price,      # price (converted to float)
                values[5],  # salt_name
                values[6].rstrip("')")  # availability (remove trailing quotes and parenthesis)
            )
            # --- Step 3: Insert into table ---
            sql = """
            INSERT INTO medicines (medicine_name, generic_substitute, dosage, brand, price, salt_name, availability)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(sql, validated_values)
        except ValueError:
            print(f"Skipping invalid row due to price conversion error: {values}")
            continue

        # The insert statement has been moved inside the try block above

# --- Step 4: Commit and close ---
connection.commit()
print("✅ Data inserted successfully!")

cursor.close()
connection.close()