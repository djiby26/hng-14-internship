import json
import uuid
import os

INPUT_FILE = "seed_profiles.json"
OUTPUT_FILE = "02_seed.sql"
TABLE_NAME = "profiles"
DB_NAME = "hng_db"


def generate_uuid7_placeholder():
    """
    UUID v7 requires a library (uuid6). Falling back to uuid4 here.
    Replace with uuid6.uuid7() if you have the package installed:
        pip install uuid6
    """
    try:
        import uuid6
        return str(uuid6.uuid7())
    except ImportError:
        return str(uuid.uuid4())


def escape(value):
    """Escape single quotes in string values."""
    if value is None:
        return "NULL"
    return str(value).replace("'", "''")


def to_sql_value(value):
    if value is None:
        return "NULL"
    if isinstance(value, bool):
        return "1" if value else "0"
    if isinstance(value, (int, float)):
        return str(value)
    return f"'{escape(value)}'"


def generate_seed_sql(input_file: str, output_file: str):
    with open(input_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    profiles = data.get("profiles", [])
    if not profiles:
        print("No profiles found in JSON.")
        return

    # Ensure output directory exists
    os.makedirs(os.path.dirname(output_file) or ".", exist_ok=True)

    columns = [
        "id",
        "name",
        "gender",
        "gender_probability",
        "age",
        "age_group",
        "country_id",
        "country_name",
        "country_probability",
    ]

    with open(output_file, "w", encoding="utf-8") as f:
        f.write(f"USE {DB_NAME};\n\n")
        f.write(f"-- Auto-generated seed from {input_file}\n")
        f.write(f"-- {len(profiles)} record(s)\n\n")

        for profile in profiles:
            record_id = generate_uuid7_placeholder()

            values = [
                to_sql_value(record_id),
                to_sql_value(profile.get("name")),
                to_sql_value(profile.get("gender")),
                to_sql_value(profile.get("gender_probability")),
                to_sql_value(profile.get("age")),
                to_sql_value(profile.get("age_group")),
                to_sql_value(profile.get("country_id")),
                to_sql_value(profile.get("country_name")),
                to_sql_value(profile.get("country_probability")),
            ]

            col_str = ", ".join(columns)
            val_str = ", ".join(values)
            f.write(f"INSERT IGNORE INTO {TABLE_NAME} ({col_str}) VALUES ({val_str});\n")

    print(f"Done. {len(profiles)} INSERT(s) written to '{output_file}'")


if __name__ == "__main__":
    generate_seed_sql(INPUT_FILE, OUTPUT_FILE)