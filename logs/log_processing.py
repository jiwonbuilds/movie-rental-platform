import sys

def calculate_avg_times(file_names):
    jdbc_times = []
    servlet_times = []

    for file_name in file_names:
        with open(file_name, 'r') as file:
            lines = file.readlines()

            for line in lines:
                if line.startswith("JDBC Time"):
                    jdbc_times.append(int(line.split(":")[1].strip()))
                elif line.startswith("Servlet Time"):
                    servlet_times.append(int(line.split(":")[1].strip()))

    if not jdbc_times or not servlet_times:
        print("No data found in the files.")
        return None

    return sum(jdbc_times) / len(jdbc_times), sum(servlet_times) / len(servlet_times)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python script.py file1.txt file2.txt")
        sys.exit(1)

    file_names = sys.argv[1:]
    average_jdbc, average_servlet = calculate_avg_times(file_names)

    if average_jdbc is not None and average_servlet is not None:
        print(f"Average JDBC Time: {average_jdbc}")
        print(f"Average Servlet Time: {average_servlet}")
